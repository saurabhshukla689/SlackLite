package org.example.gateway.config;

import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import com.apollographql.federation.graphqljava.tracing.FederatedTracingInstrumentation;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Configuration
public class GraphQLFederationConfig {

    private static final Logger log = LoggerFactory.getLogger(GraphQLFederationConfig.class);

    @Autowired
    private FederationServiceConfig serviceConfig;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB buffer size
                .build();
    }

    @Bean
    public GraphQL federatedGraphQL(WebClient webClient) {
        log.info("Initializing GraphQL Federation Gateway");

        // Fetch schemas from all federated services
        Map<String, String> serviceSchemas = fetchServiceSchemas(webClient);

        // Merge all schemas into a unified schema
        TypeDefinitionRegistry typeRegistry = mergeSchemas(serviceSchemas);

        // Build runtime wiring with federation resolvers
        RuntimeWiring runtimeWiring = buildRuntimeWiring(webClient);

        // Create federated schema with entity resolution
        GraphQLSchema federatedSchema = Federation.transform(typeRegistry, runtimeWiring)
                .fetchEntities(env -> fetchEntities(env, webClient))
                .resolveEntityType(env -> {
                    final Map<String, Object> entity = env.getObject();
                    String typename = (String) entity.get("__typename");
                    return env.getSchema().getObjectType(typename);
                })
                .build();

        // Build GraphQL with federation tracing
        return GraphQL.newGraphQL(federatedSchema)
                .instrumentation(new FederatedTracingInstrumentation())
                .build();
    }

    /**
     * Fetches SDL schemas from all federated services
     */
    private Map<String, String> fetchServiceSchemas(WebClient webClient) {
        Map<String, String> schemas = new HashMap<>();

        serviceConfig.getServices().forEach((key, service) -> {
            log.info("Fetching schema from service: {} at {}", service.getName(), service.getUrl());

            try {
                String schema = webClient.post()
                        .uri(service.getUrl())
                        .bodyValue(Map.of("query", "{ _service { sdl } }"))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .timeout(Duration.ofSeconds(5))
                        .map(response -> {
                            Map<String, Object> data = (Map<String, Object>) response.get("data");
                            Map<String, String> serviceData = (Map<String, String>) data.get("_service");
                            return serviceData.get("sdl");
                        })
                        .block();

                schemas.put(service.getName(), schema);
                log.info("Successfully fetched schema from {}", service.getName());
            } catch (Exception e) {
                log.error("Failed to fetch schema from service: {}", service.getName(), e);
                // Provide a minimal fallback schema
                schemas.put(service.getName(), getMinimalSchema(service.getName()));
            }
        });

        return schemas;
    }

    /**
     * Merges all service schemas into a unified TypeDefinitionRegistry
     */
    private TypeDefinitionRegistry mergeSchemas(Map<String, String> serviceSchemas) {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

        // Add base federation types
        String federationSchema = """
            scalar _Any
            scalar _FieldSet
            
            directive @external on FIELD_DEFINITION
            directive @requires(fields: _FieldSet!) on FIELD_DEFINITION
            directive @provides(fields: _FieldSet!) on FIELD_DEFINITION
            directive @key(fields: _FieldSet!) on OBJECT | INTERFACE
            directive @extends on OBJECT | INTERFACE
            
            type Query {
                _service: _Service!
                _entities(representations: [_Any!]!): [_Entity]!
            }
            
            type _Service {
                sdl: String!
            }
            
            union _Entity
            """;

        typeRegistry.merge(schemaParser.parse(federationSchema));

        // Merge each service schema
        serviceSchemas.forEach((serviceName, schema) -> {
            try {
                TypeDefinitionRegistry serviceRegistry = schemaParser.parse(schema);
                typeRegistry.merge(serviceRegistry);
                log.info("Merged schema from service: {}", serviceName);
            } catch (Exception e) {
                log.error("Failed to merge schema from service: {}", serviceName, e);
            }
        });

        return typeRegistry;
    }

    /**
     * Builds runtime wiring with data fetchers for all services
     */
    private RuntimeWiring buildRuntimeWiring(WebClient webClient) {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();

        // Add federation introspection data fetchers
        builder.type("Query", typeWiring -> typeWiring
                .dataFetcher("_service", env -> Map.of("sdl", ""))
                .dataFetcher("_entities", env -> fetchEntities(env, webClient))
        );

        // Configure data fetchers for each service
        serviceConfig.getServices().forEach((key, service) -> {
            configureServiceDataFetchers(builder, service, webClient);
        });

        return builder.build();
    }

    /**
     * Configures data fetchers for a specific service
     */
    private void configureServiceDataFetchers(RuntimeWiring.Builder builder,
                                              FederationServiceConfig.ServiceDefinition service,
                                              WebClient webClient) {
        String serviceName = service.getName();

        // Configure Query resolvers based on service
        switch (serviceName) {
            case "auth-service":
                configureAuthServiceFetchers(builder, service, webClient);
                break;
            case "channel-service":
                configureChannelServiceFetchers(builder, service, webClient);
                break;
            case "message-service":
                configureMessageServiceFetchers(builder, service, webClient);
                break;
            default:
                log.warn("No specific configuration for service: {}", serviceName);
        }
    }

    private void configureAuthServiceFetchers(RuntimeWiring.Builder builder,
                                              FederationServiceConfig.ServiceDefinition service,
                                              WebClient webClient) {
        builder.type("Query", typeWiring -> typeWiring
                .dataFetcher("me", createServiceDataFetcher(service, "me", webClient))
                .dataFetcher("user", createServiceDataFetcher(service, "user", webClient))
                .dataFetcher("users", createServiceDataFetcher(service, "users", webClient))
        );

        builder.type("Mutation", typeWiring -> typeWiring
                .dataFetcher("login", createServiceDataFetcher(service, "login", webClient))
                .dataFetcher("register", createServiceDataFetcher(service, "register", webClient))
                .dataFetcher("logout", createServiceDataFetcher(service, "logout", webClient))
        );
    }

    private void configureChannelServiceFetchers(RuntimeWiring.Builder builder,
                                                 FederationServiceConfig.ServiceDefinition service,
                                                 WebClient webClient) {
        builder.type("Query", typeWiring -> typeWiring
                .dataFetcher("channels", createServiceDataFetcher(service, "channels", webClient))
                .dataFetcher("channel", createServiceDataFetcher(service, "channel", webClient))
                .dataFetcher("myChannels", createServiceDataFetcher(service, "myChannels", webClient))
        );

        builder.type("Mutation", typeWiring -> typeWiring
                .dataFetcher("createChannel", createServiceDataFetcher(service, "createChannel", webClient))
                .dataFetcher("joinChannel", createServiceDataFetcher(service, "joinChannel", webClient))
                .dataFetcher("leaveChannel", createServiceDataFetcher(service, "leaveChannel", webClient))
        );
    }

    private void configureMessageServiceFetchers(RuntimeWiring.Builder builder,
                                                 FederationServiceConfig.ServiceDefinition service,
                                                 WebClient webClient) {
        builder.type("Query", typeWiring -> typeWiring
                .dataFetcher("messages", createServiceDataFetcher(service, "messages", webClient))
                .dataFetcher("message", createServiceDataFetcher(service, "message", webClient))
        );

        builder.type("Mutation", typeWiring -> typeWiring
                .dataFetcher("sendMessage", createServiceDataFetcher(service, "sendMessage", webClient))
                .dataFetcher("editMessage", createServiceDataFetcher(service, "editMessage", webClient))
                .dataFetcher("deleteMessage", createServiceDataFetcher(service, "deleteMessage", webClient))
        );

        builder.type("Subscription", typeWiring -> typeWiring
                .dataFetcher("messageAdded", createServiceDataFetcher(service, "messageAdded", webClient))
                .dataFetcher("messageUpdated", createServiceDataFetcher(service, "messageUpdated", webClient))
        );
    }

    /**
     * Creates a generic data fetcher that forwards requests to a service
     */
    private DataFetcher<?> createServiceDataFetcher(FederationServiceConfig.ServiceDefinition service,
                                                    String fieldName,
                                                    WebClient webClient) {
        return env -> {
            // Build GraphQL query from the current environment
            String query = buildQueryFromEnvironment(env, fieldName);
            Map<String, Object> variables = env.getVariables();

            // Forward the request to the appropriate service
            return webClient.post()
                    .uri(service.getUrl())
                    .bodyValue(Map.of(
                            "query", query,
                            "variables", variables
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5))
                    .map(response -> {
                        Map<String, Object> data = (Map<String, Object>) response.get("data");
                        return data != null ? data.get(fieldName) : null;
                    })
                    .toFuture();
        };
    }

    /**
     * Fetches entities from appropriate services based on __typename
     */
    private CompletableFuture<List<Object>> fetchEntities(graphql.schema.DataFetchingEnvironment env,
                                                          WebClient webClient) {
        List<Map<String, Object>> representations = env.getArgument("representations");

        // Group representations by typename to batch requests
        Map<String, List<Map<String, Object>>> byTypename = representations.stream()
                .collect(Collectors.groupingBy(r -> (String) r.get("__typename")));

        // Fetch entities from each service in parallel
        List<CompletableFuture<List<Object>>> futures = byTypename.entrySet().stream()
                .map(entry -> fetchEntitiesForType(entry.getKey(), entry.getValue(), webClient))
                .collect(Collectors.toList());

        // Combine all results
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
    }

    /**
     * Fetches entities of a specific type from the appropriate service
     */
    private CompletableFuture<List<Object>> fetchEntitiesForType(String typename,
                                                                 List<Map<String, Object>> representations,
                                                                 WebClient webClient) {
        // Determine which service handles this type
        String serviceUrl = determineServiceForType(typename);

        if (serviceUrl == null) {
            log.warn("No service found for type: {}", typename);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        // Build _entities query
        String query = """
            query GetEntities($representations: [_Any!]!) {
                _entities(representations: $representations)
            }
            """;

        return webClient.post()
                .uri(serviceUrl)
                .bodyValue(Map.of(
                        "query", query,
                        "variables", Map.of("representations", representations)
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(5))
                .map(response -> {
                    Map<String, Object> data = (Map<String, Object>) response.get("data");
                    return data != null ? (List<Object>) data.get("_entities") : Collections.emptyList();
                })
                .onErrorReturn(Collections.emptyList())
                .toFuture();
    }

    /**
     * Determines which service handles a specific type
     */
    private String determineServiceForType(String typename) {
        // Map types to services based on your schema design
        Map<String, String> typeToService = Map.of(
                "User", "auth-service",
                "Channel", "channel-service",
                "Message", "message-service",
                "Member", "channel-service",
                "Reaction", "message-service"
        );

        String serviceName = typeToService.get(typename);
        if (serviceName != null) {
            FederationServiceConfig.ServiceDefinition service =
                    serviceConfig.getServices().get(serviceName);
            return service != null ? service.getUrl() : null;
        }

        return null;
    }

    /**
     * Builds a GraphQL query string from the DataFetchingEnvironment
     */
    private String buildQueryFromEnvironment(graphql.schema.DataFetchingEnvironment env, String fieldName) {
        // This is a simplified version - you might need more sophisticated query building
        Map<String, Object> arguments = env.getArguments();

        if (arguments.isEmpty()) {
            return String.format("{ %s }", fieldName);
        }

        String args = arguments.entrySet().stream()
                .map(entry -> String.format("%s: $%s", entry.getKey(), entry.getKey()))
                .collect(Collectors.joining(", "));

        return String.format("query($%s) { %s(%s) }",
                arguments.keySet().stream().collect(Collectors.joining(", $")),
                fieldName,
                args);
    }

    /**
     * Provides a minimal schema fallback for services that are unavailable
     */
    private String getMinimalSchema(String serviceName) {
        return String.format("""
            extend type Query {
                _service: _Service!
            }
            
            type _Service {
                sdl: String!
            }
            """, serviceName);
    }
}
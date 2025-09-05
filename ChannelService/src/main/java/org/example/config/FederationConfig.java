package org.example.config;

// src/main/java/your/pkg/graphql/FederationConfig.java
import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import graphql.schema.GraphQLObjectType;
import jakarta.annotation.PostConstruct;
import org.example.entity.Channel;
import org.example.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.data.federation.FederationSchemaFactory;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


//@Configuration
//public class FederationConfig {
//
//    @PostConstruct
//    public void init() {
//        System.out.println("🔥 FederationConfig loaded!");
//    }
//
//    @Bean
//    public GraphQlSourceBuilderCustomizer customizer(FederationSchemaFactory factory) {
//        System.out.println("🔥 Creating customizer with factory: " + factory);
//        return builder -> builder.schemaFactory(factory::createGraphQLSchema);
//    }
//
//    @Bean
//    public FederationSchemaFactory schemaFactory() {
//        System.out.println("Creating FederationSchemaFactory");
//        return new FederationSchemaFactory();
//    }
//}

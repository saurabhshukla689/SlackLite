package org.example.authservice.config;


import com.apollographql.federation.graphqljava.Federation;
import com.apollographql.federation.graphqljava._Entity;
import graphql.schema.GraphQLObjectType;
import jakarta.annotation.PostConstruct;
import org.example.authservice.entity.User;
import org.example.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.data.federation.FederationSchemaFactory;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;

import java.util.List;
import java.util.Map;
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
//        System.out.println("🔥 Creating FederationSchemaFactory");
//        return new FederationSchemaFactory();
//    }
//}

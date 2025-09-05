package org.example.gateway.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "federation")
public class FederationServiceConfig {

    private Map<String, ServiceDefinition> services = new HashMap<>();

    public static class ServiceDefinition {
        private String name;
        private String url;

        public ServiceDefinition() {}

        public ServiceDefinition(String name, String url) {
            this.name = name;
            this.url = url;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    // Getters and Setters
    public Map<String, ServiceDefinition> getServices() { return services; }
    public void setServices(Map<String, ServiceDefinition> services) { this.services = services; }
}

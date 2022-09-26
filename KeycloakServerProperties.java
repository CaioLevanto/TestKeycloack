package com.example.demo.modules.keycloack;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak.server")
public class KeycloakServerProperties {
    String contextPath = "/auth";
    String realmImportFile = "baeldung-realm.json";
    AdminUser adminUser = new AdminUser();

    public String getContextPath() {
        return contextPath;
    }

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public String getRealmImportFile() {
        return realmImportFile;
    }

    // getters and setters

    public static class AdminUser {
        String username = "admin";
        String password = "admin";

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        // getters and setters
    }
}

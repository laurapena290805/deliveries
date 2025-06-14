package com.nova.deliveries.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// UserClient.java
@Component
public class UserClient {
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserClient(RestTemplate restTemplate, @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    // UserClient.java - Métodos adicionales
    public boolean isRepartidor(Long userId) {
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    userServiceUrl + "/api/users/" + userId + "/has-role?role=REPARTIDOR",
                    Boolean.class
            );
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar rol de repartidor: " + e.getMessage(), e);
        }
    }

    public String getUserName(Long userId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    userServiceUrl + "/api/users/" + userId + "/name",
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener nombre de usuario: " + e.getMessage(), e);
        }
    }
}

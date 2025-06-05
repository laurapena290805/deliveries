package com.nova.deliveries.client;

import com.nova.deliveries.dto.OrderResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String orderServiceUrl;

    public OrderClient(RestTemplate restTemplate, @Value("${order.service.url}") String orderServiceUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
    }

    public boolean existsOrder(Long orderId) {
        System.out.println("DEBUG - OrderClient - ID recibido: " + orderId);

        if (orderId == null) {
            System.err.println("ERROR: El ID de orden es nulo en OrderClient");
            return false;
        }
        try {
            ResponseEntity<OrderResponseDTO> response = restTemplate.getForEntity(
                    orderServiceUrl + "/api/ordenes/" + orderId,
                    OrderResponseDTO.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio de órdenes: " + e.getMessage(), e);
        }
    }
}

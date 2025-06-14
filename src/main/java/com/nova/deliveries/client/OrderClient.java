package com.nova.deliveries.client;

import com.nova.deliveries.dto.OrderResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    private final RestTemplate restTemplate;
    private final String orderServiceUrl;
    private final HttpServletRequest request;

    public OrderClient(RestTemplate restTemplate,
                       @Value("${order.service.url}") String orderServiceUrl,
                       HttpServletRequest request) {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
        this.request = request;
    }

    public boolean existsOrder(Long orderId) {
        System.out.println("DEBUG - OrderClient - ID recibido: " + orderId);

        if (orderId == null) {
            System.err.println("ERROR: El ID de orden es nulo en OrderClient");
            return false;
        }

        try {
            // Obtener el token del request actual
            String token = request.getHeader("Authorization");

            HttpHeaders headers = new HttpHeaders();
            if (token != null) {
                headers.set("Authorization", token);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<OrderResponseDTO> response = restTemplate.exchange(
                    orderServiceUrl + "/api/ordenes/" + orderId,
                    HttpMethod.GET,
                    entity,
                    OrderResponseDTO.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio de órdenes: " + e.getMessage(), e);
        }
    }

    public OrderResponseDTO getOrderStatus(Long orderId) {
        try {
            // Obtener el token del request actual
            String token = request.getHeader("Authorization");

            HttpHeaders headers = new HttpHeaders();
            if (token != null) {
                headers.set("Authorization", token);
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<OrderResponseDTO> response = restTemplate.exchange(
                    orderServiceUrl + "/api/ordenes/" + orderId,
                    HttpMethod.GET,
                    entity,
                    OrderResponseDTO.class
            );

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("La orden con ID " + orderId + " no existe");
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio de órdenes: " + e.getMessage(), e);
        }
    }
}
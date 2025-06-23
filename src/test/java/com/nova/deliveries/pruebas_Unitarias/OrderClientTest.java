package com.nova.deliveries.pruebas_Unitarias;

import com.nova.deliveries.client.OrderClient;
import com.nova.deliveries.dto.OrderResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private OrderClient orderClient;

    private final String orderServiceUrl = "http://order-service";

    @BeforeEach
    void setUp() {
        orderClient = new OrderClient(restTemplate, orderServiceUrl, request);
    }

    @Test
    void existsOrder_WhenOrderExists_ShouldReturnTrue() {
        Long orderId = 1L;
        String url = orderServiceUrl + "/api/ordenes/" + orderId;

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(OrderResponseDTO.class)))
                .thenReturn(new ResponseEntity<>(new OrderResponseDTO(), HttpStatus.OK));

        boolean result = orderClient.existsOrder(orderId);

        assertTrue(result);
        verify(request).getHeader("Authorization");
        verify(restTemplate).exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(OrderResponseDTO.class));
    }

    @Test
    void existsOrder_WhenOrderNotExists_ShouldReturnFalse() {
        Long orderId = 1L;
        String url = orderServiceUrl + "/api/ordenes/" + orderId;

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(OrderResponseDTO.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        new HttpHeaders(),
                        null,
                        null));

        boolean result = orderClient.existsOrder(orderId);

        assertFalse(result);
    }

    @Test
    void existsOrder_WhenServiceError_ShouldThrowRuntimeException() {
        Long orderId = 1L;
        String url = orderServiceUrl + "/api/ordenes/" + orderId;

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(OrderResponseDTO.class)))
                .thenThrow(new RuntimeException("Error de conexión"));

        assertThrows(RuntimeException.class, () -> {
            orderClient.existsOrder(orderId);
        });
    }

    @Test
    void getOrderStatus_WhenOrderExists_ShouldReturnOrder() {
        Long orderId = 1L;
        String url = orderServiceUrl + "/api/ordenes/" + orderId;
        OrderResponseDTO expectedResponse = new OrderResponseDTO();
        expectedResponse.setOrderId(orderId);

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(OrderResponseDTO.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        OrderResponseDTO result = orderClient.getOrderStatus(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
    }

    @Test
    void getOrderStatus_WhenOrderNotExists_ShouldThrowIllegalArgumentException() {
        Long orderId = 1L;
        String url = orderServiceUrl + "/api/ordenes/" + orderId;

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(OrderResponseDTO.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        new HttpHeaders(),
                        null,
                        null));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderClient.getOrderStatus(orderId);
        });

        assertEquals("La orden con ID " + orderId + " no existe", exception.getMessage());
    }

    @Test
    void getOrderStatus_WhenServiceError_ShouldThrowRuntimeException() {
        Long orderId = 1L;
        String url = orderServiceUrl + "/api/ordenes/" + orderId;

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(OrderResponseDTO.class)))
                .thenThrow(new RuntimeException("Error de conexión"));

        assertThrows(RuntimeException.class, () -> {
            orderClient.getOrderStatus(orderId);
        });
    }

    @Test
    void existsOrder_WhenOrderIdIsNull_ShouldReturnFalse() {
        boolean result = orderClient.existsOrder(null);
        assertFalse(result);
    }
}
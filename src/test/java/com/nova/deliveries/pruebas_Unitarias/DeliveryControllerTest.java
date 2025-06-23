package com.nova.deliveries.pruebas_Unitarias;

import com.nova.deliveries.controller.DeliveryController;
import com.nova.deliveries.dto.DeliveryResponseDTO;
import com.nova.deliveries.entity.DeliveryStatus;
import com.nova.deliveries.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {

    @Mock
    private DeliveryService deliveryService;

    @InjectMocks
    private DeliveryController deliveryController;

    private DeliveryResponseDTO deliveryResponseDTO;

    @BeforeEach
    void setUp() {
        deliveryResponseDTO = new DeliveryResponseDTO();
        deliveryResponseDTO.setId(1L);
        deliveryResponseDTO.setOrdenId(1L);
        deliveryResponseDTO.setDireccion("Calle Falsa 123");
        deliveryResponseDTO.setFechaEstimada(LocalDate.now().plusDays(2));
        deliveryResponseDTO.setEstado(DeliveryStatus.PENDIENTE);
    }

    @Test
    void crearEntrega_ShouldReturnCreated() {
        Map<String, Object> requestMap = Map.of(
                "ordenId", "1",
                "direccion", "Calle Falsa 123",
                "fechaEstimada", LocalDate.now().plusDays(2).toString()
        );

        when(deliveryService.crearEntrega(any())).thenReturn(deliveryResponseDTO);

        ResponseEntity<?> response = deliveryController.crearEntrega(requestMap);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(deliveryService).crearEntrega(any());
    }

    @Test
    void cambiarEstado_ShouldReturnOk() {
        when(deliveryService.cambiarEstado(1L, DeliveryStatus.EN_CAMINO))
                .thenReturn(deliveryResponseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.cambiarEstado(1L, DeliveryStatus.EN_CAMINO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(deliveryService).cambiarEstado(1L, DeliveryStatus.EN_CAMINO);
    }

    @Test
    void obtenerPorOrden_ShouldReturnList() {
        when(deliveryService.obtenerPorOrden(1L))
                .thenReturn(Collections.singletonList(deliveryResponseDTO));

        ResponseEntity<List<DeliveryResponseDTO>> response =
                deliveryController.obtenerPorOrden(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(deliveryService).obtenerPorOrden(1L);
    }

    @Test
    void asignarRepartidor_ShouldReturnOk() {
        when(deliveryService.asignarRepartidor(1L, 2L))
                .thenReturn(deliveryResponseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.asignarRepartidor(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(deliveryService).asignarRepartidor(1L, 2L);
    }

    @Test
    void escogerEntrega_ShouldReturnOk() {
        when(deliveryService.escogerEntrega(2L, 1L)) // Cambiar el orden de los parámetros
                .thenReturn(deliveryResponseDTO);

        ResponseEntity<DeliveryResponseDTO> response =
                deliveryController.escogerEntrega(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(deliveryService).escogerEntrega(2L, 1L); // Verificar con el nuevo orden
    }

    @Test
    void obtenerEntregasDisponibles_ShouldReturnList() {
        when(deliveryService.obtenerEntregasDisponibles())
            .thenReturn(Collections.singletonList(deliveryResponseDTO));

        ResponseEntity<List<DeliveryResponseDTO>> response =
                deliveryController.obtenerEntregasDisponibles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(deliveryService).obtenerEntregasDisponibles();
    }
}
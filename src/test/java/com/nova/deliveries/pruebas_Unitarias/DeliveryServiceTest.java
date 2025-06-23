package com.nova.deliveries.pruebas_Unitarias;

import com.nova.deliveries.client.OrderClient;
import com.nova.deliveries.client.UserClient;
import com.nova.deliveries.dto.DeliveryRequestDTO;
import com.nova.deliveries.dto.DeliveryResponseDTO;
import com.nova.deliveries.dto.OrderResponseDTO;
import com.nova.deliveries.entity.Delivery;
import com.nova.deliveries.entity.DeliveryStatus;
import com.nova.deliveries.repository.DeliveryRepository;
import com.nova.deliveries.service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private DeliveryService deliveryService;

    private DeliveryRequestDTO deliveryRequestDTO;
    private Delivery delivery;
    private OrderResponseDTO orderResponseDTO;

    @BeforeEach
    void setUp() {
        deliveryRequestDTO = new DeliveryRequestDTO();
        deliveryRequestDTO.setOrdenId(1L);
        deliveryRequestDTO.setDireccion("Calle Falsa 123");
        deliveryRequestDTO.setFechaEstimada(LocalDate.now().plusDays(2));

        delivery = new Delivery();
        delivery.setId(1L);
        delivery.setOrdenId(1L);
        delivery.setDireccion("Calle Falsa 123");
        delivery.setFechaEstimada(LocalDate.now().plusDays(2));
        delivery.setEstado(DeliveryStatus.PENDIENTE);

        orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setOrderId(1L);
        orderResponseDTO.setEstado("LISTA");
    }

    @Test
    void crearEntrega_WhenOrderIsReady_ShouldCreateDelivery() {
        when(orderClient.getOrderStatus(anyLong())).thenReturn(orderResponseDTO);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDTO result = deliveryService.crearEntrega(deliveryRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getOrdenId());
        assertEquals("Calle Falsa 123", result.getDireccion());
        assertEquals(DeliveryStatus.PENDIENTE, result.getEstado());

        verify(orderClient).getOrderStatus(1L);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void crearEntrega_WhenOrderNotReady_ShouldThrowException() {
        orderResponseDTO.setEstado("PENDIENTE");
        when(orderClient.getOrderStatus(anyLong())).thenReturn(orderResponseDTO);

        assertThrows(IllegalArgumentException.class, () -> {
            deliveryService.crearEntrega(deliveryRequestDTO);
        });

        verify(orderClient).getOrderStatus(1L);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void asignarRepartidor_WhenValid_ShouldAssignDeliveryPerson() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(userClient.isRepartidor(2L)).thenReturn(true);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDTO result = deliveryService.asignarRepartidor(1L, 2L);

        assertNotNull(result);
        assertEquals(2L, result.getRepartidorId());
        assertEquals(DeliveryStatus.EN_CAMINO, result.getEstado());

        verify(deliveryRepository).findById(1L);
        verify(userClient).isRepartidor(2L);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void asignarRepartidor_WhenNotDeliveryPerson_ShouldThrowException() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(userClient.isRepartidor(2L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            deliveryService.asignarRepartidor(1L, 2L);
        });

        verify(deliveryRepository).findById(1L);
        verify(userClient).isRepartidor(2L);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void escogerEntrega_WhenValid_ShouldAssignDeliveryToPerson() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(userClient.isRepartidor(2L)).thenReturn(true);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDTO result = deliveryService.escogerEntrega(2L, 1L);

        assertNotNull(result);
        assertEquals(2L, result.getRepartidorId());
        assertEquals(DeliveryStatus.EN_CAMINO, result.getEstado());

        verify(deliveryRepository).findById(1L);
        verify(userClient).isRepartidor(2L);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void escogerEntrega_WhenAlreadyAssigned_ShouldThrowException() {
        delivery.setRepartidorId(3L);
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));

        assertThrows(IllegalArgumentException.class, () -> {
            deliveryService.escogerEntrega(2L, 1L);
        });

        verify(deliveryRepository).findById(1L);
        verify(userClient, never()).isRepartidor(any());
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void obtenerEntregasDisponibles_ShouldReturnAvailableDeliveries() {
        when(deliveryRepository.findByRepartidorIdIsNull()).thenReturn(Collections.singletonList(delivery));

        List<DeliveryResponseDTO> result = deliveryService.obtenerEntregasDisponibles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrdenId());

        verify(deliveryRepository).findByRepartidorIdIsNull();
    }

    @Test
    void cambiarEstado_ShouldUpdateDeliveryStatus() {
        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        DeliveryResponseDTO result = deliveryService.cambiarEstado(1L, DeliveryStatus.ENTREGADA);

        assertNotNull(result);
        assertEquals(DeliveryStatus.ENTREGADA, result.getEstado());

        verify(deliveryRepository).findById(1L);
        verify(deliveryRepository).save(any(Delivery.class));
    }
}
package com.nova.deliveries.service;

import com.nova.deliveries.client.OrderClient;
import com.nova.deliveries.client.UserClient;
import com.nova.deliveries.dto.DeliveryRequestDTO;
import com.nova.deliveries.dto.DeliveryResponseDTO;
import com.nova.deliveries.dto.OrderResponseDTO;
import com.nova.deliveries.entity.Delivery;
import com.nova.deliveries.entity.DeliveryStatus;
import com.nova.deliveries.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderClient orderClient;

    public DeliveryResponseDTO crearEntrega(DeliveryRequestDTO requestDTO) {
        // Verificar que la orden existe y está lista
        OrderResponseDTO orden = orderClient.getOrderStatus(requestDTO.getOrdenId());
        if (!"LISTA".equalsIgnoreCase(orden.getEstado())) {
            throw new IllegalArgumentException("La orden con ID " + requestDTO.getOrdenId() +
                    " no está lista para entrega. Estado actual: " + orden.getEstado());
        }

        Delivery delivery = new Delivery();
        delivery.setOrdenId(requestDTO.getOrdenId());
        delivery.setDireccion(requestDTO.getDireccion());
        delivery.setFechaEstimada(requestDTO.getFechaEstimada());
        delivery.setEstado(DeliveryStatus.PENDIENTE);

        Delivery saved = deliveryRepository.save(delivery);
        return mapToDTO(saved);
    }

    public DeliveryResponseDTO asignarRepartidor(Long deliveryId, Long repartidorId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));

        // Verificar que el usuario es repartidor
        if (!userClient.isRepartidor(repartidorId)) {
            throw new IllegalArgumentException("El usuario con ID " + repartidorId +
                    " no tiene rol de repartidor");
        }

        delivery.setRepartidorId(repartidorId);
        delivery.setEstado(DeliveryStatus.EN_CAMINO);

        Delivery updated = deliveryRepository.save(delivery);
        return mapToDTO(updated);
    }

    public DeliveryResponseDTO escogerEntrega(Long repartidorId, Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));

        // Verificar que la entrega no tiene repartidor asignado
        if (delivery.getRepartidorId() != null) {
            throw new IllegalArgumentException("Esta entrega ya tiene un repartidor asignado");
        }

        // Verificar que el usuario es repartidor
        if (!userClient.isRepartidor(repartidorId)) {
            throw new IllegalArgumentException("El usuario con ID " + repartidorId +
                    " no tiene rol de repartidor");
        }

        delivery.setRepartidorId(repartidorId);
        delivery.setEstado(DeliveryStatus.EN_CAMINO);

        Delivery updated = deliveryRepository.save(delivery);
        return mapToDTO(updated);
    }

    public List<DeliveryResponseDTO> obtenerEntregasDisponibles() {
        return deliveryRepository.findByRepartidorIdIsNull().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponseDTO> obtenerEntregasPorRepartidor(Long repartidorId) {
        return deliveryRepository.findByRepartidorId(repartidorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DeliveryResponseDTO cambiarEstado(Long id, DeliveryStatus nuevoEstado) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));
        delivery.setEstado(nuevoEstado);
        return mapToDTO(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponseDTO> obtenerPorOrden(Long ordenId) {
        return deliveryRepository.findByOrdenId(ordenId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private DeliveryResponseDTO mapToDTO(Delivery delivery) {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();
        dto.setId(delivery.getId());
        dto.setOrdenId(delivery.getOrdenId());
        dto.setDireccion(delivery.getDireccion());
        dto.setFechaEstimada(delivery.getFechaEstimada());
        dto.setEstado(delivery.getEstado());
        dto.setRepartidorId(delivery.getRepartidorId());

        if (delivery.getRepartidorId() != null) {
            try {
                dto.setNombreRepartidor(userClient.getUserName(delivery.getRepartidorId()));
            } catch (Exception e) {
                dto.setNombreRepartidor("Desconocido");
            }
        }

        return dto;
    }

    public List<DeliveryResponseDTO> listarTodasLasEntregas() {
        return deliveryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<DeliveryResponseDTO> listarEntregasPaginado(Pageable pageable) {
        return deliveryRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
}
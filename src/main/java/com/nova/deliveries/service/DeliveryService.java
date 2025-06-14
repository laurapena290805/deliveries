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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    @Autowired
    private DeliveryRepository entregaRepository;
    private UserClient userClient;

    @Autowired
    private OrderClient orderClient;

    // DeliveryService.java - Modificar el método crearEntrega
    public DeliveryResponseDTO crearEntrega(DeliveryRequestDTO requestDTO) {
        // Verificar que la orden existe y está lista
        OrderResponseDTO orden = orderClient.getOrderStatus(requestDTO.getOrdenId());
        //if (!"LISTA".equalsIgnoreCase(orden.getStatus())) {
          //  throw new IllegalArgumentException("La orden con ID " + requestDTO.getOrdenId() +
            //        " no está lista para entrega. Estado actual: " + orden.getStatus());
        //}

        Delivery delivery = new Delivery();
        delivery.setOrdenId(requestDTO.getOrdenId());
        delivery.setDireccion(requestDTO.getDireccion());
        delivery.setFechaEstimada(requestDTO.getFechaEstimada());
        delivery.setEstado(DeliveryStatus.PENDIENTE);

        // Manejo del repartidor
        if (requestDTO.getRepartidorId() != null) {
            if (requestDTO.isSolicitarAsignacion()) {
                delivery.setAsignacionPendiente(true);
                delivery.setRepartidorId(requestDTO.getRepartidorId());
            } else {
                // Aquí deberías verificar que el usuario tiene rol de repartidor
                // Esto requeriría integración con el servicio de usuarios
                delivery.setRepartidorId(requestDTO.getRepartidorId());
            }
        }

        Delivery saved = entregaRepository.save(delivery);
        return mapToDTO(saved);
    }

    // Agregar método para aprobar repartidor
    public DeliveryResponseDTO aprobarRepartidor(Long deliveryId) {
        Delivery delivery = entregaRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));

        if (!delivery.isAsignacionPendiente()) {
            throw new IllegalArgumentException("Esta entrega no tiene asignaciones pendientes");
        }

        delivery.setAsignacionPendiente(false);
        return mapToDTO(entregaRepository.save(delivery));
    }

    // DeliveryService.java - Métodos adicionales
    public DeliveryResponseDTO asignarRepartidor(Long deliveryId, DeliveryRequestDTO requestDTO) {
        // Verificar que la entrega existe
        Delivery delivery = entregaRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));

        // Verificar que el usuario es repartidor (integrado con servicio de usuarios)
        if (requestDTO.getRepartidorId() != null && !userClient.isRepartidor(requestDTO.getRepartidorId())) {
            throw new IllegalArgumentException("El usuario con ID " + requestDTO.getRepartidorId() +
                    " no tiene rol de repartidor");
        }

        // Si es solicitud de asignación (necesita aprobación)
        if (requestDTO.isSolicitarAsignacion()) {
            delivery.setAsignacionPendiente(true);
            delivery.setRepartidorId(requestDTO.getRepartidorId());
            delivery.setEstado(DeliveryStatus.PENDIENTE_APROBACION);
        }
        // Si es asignación directa (por admin)
        else {
            delivery.setRepartidorId(requestDTO.getRepartidorId());
            delivery.setAsignacionPendiente(false);
            delivery.setEstado(DeliveryStatus.EN_CAMINO);
        }

        Delivery updated = entregaRepository.save(delivery);
        return mapToDTO(updated);
    }

    public List<DeliveryResponseDTO> obtenerPorRepartidor(Long repartidorId, boolean soloPendientes) {
        List<Delivery> deliveries;

        if (soloPendientes) {
            // Entregas asignadas a este repartidor que están pendientes de aprobación
            deliveries = entregaRepository.findByRepartidorIdAndAsignacionPendiente(repartidorId, true);
        } else {
            // Todas las entregas asignadas a este repartidor
            deliveries = entregaRepository.findByRepartidorId(repartidorId);
        }

        return deliveries.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponseDTO> obtenerEntregasPendientesAprobacion() {
        // Para que el administrador vea qué entregas necesitan aprobación
        return entregaRepository.findByAsignacionPendiente(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public DeliveryResponseDTO cambiarEstado(Long id, DeliveryStatus nuevoEstado) {
        Delivery entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));
        entrega.setEstado(nuevoEstado);
        return mapToDTO(entregaRepository.save(entrega));
    }

    public List<DeliveryResponseDTO> obtenerPorOrden(Long ordenId) {
        return entregaRepository.findByOrdenId(ordenId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private DeliveryResponseDTO mapToDTO(Delivery entrega) {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();
        dto.setId(entrega.getId());
        dto.setOrdenId(entrega.getOrdenId());
        dto.setDireccion(entrega.getDireccion());
        dto.setFechaEstimada(entrega.getFechaEstimada());
        dto.setEstado(entrega.getEstado());
        dto.setRepartidorId(entrega.getRepartidorId());
        dto.setAsignacionPendiente(entrega.isAsignacionPendiente());

        // Opcional: Obtener nombre del repartidor desde el servicio de usuarios
        if (entrega.getRepartidorId() != null) {
            try {
                dto.setNombreRepartidor(userClient.getUserName(entrega.getRepartidorId()));
            } catch (Exception e) {
                dto.setNombreRepartidor("Desconocido");
            }
        }

        return dto;
    }

    public List<DeliveryResponseDTO> listarTodasLasEntregas() {
        return entregaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}


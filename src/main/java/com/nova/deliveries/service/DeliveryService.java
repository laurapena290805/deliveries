package com.nova.deliveries.service;

import com.nova.deliveries.client.OrderClient;
import com.nova.deliveries.dto.DeliveryRequestDTO;
import com.nova.deliveries.dto.DeliveryResponseDTO;
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

    @Autowired
    private OrderClient orderClient;

    public DeliveryResponseDTO crearEntrega(DeliveryRequestDTO requestDTO) {
        System.out.println("DEBUG - DTO en Service:");
        System.out.println("OrdenID: " + requestDTO.getOrdenId());

        if (requestDTO.getOrdenId() == null) {
            throw new IllegalArgumentException("El ID de la orden es nulo en el servicio");
        }
        try {
            if (!orderClient.existsOrder(requestDTO.getOrdenId())) {
                throw new IllegalArgumentException("La orden con ID " + requestDTO.getOrdenId() + " no existe");
            }

            Delivery delivery = new Delivery();
            delivery.setOrdenId(requestDTO.getOrdenId());
            delivery.setDireccion(requestDTO.getDireccion());
            delivery.setFechaEstimada(requestDTO.getFechaEstimada());
            delivery.setEstado(DeliveryStatus.PENDIENTE);

            Delivery saved = entregaRepository.save(delivery);

            return mapToDTO(saved);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al crear la entrega: " + e.getMessage(), e);
        }
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
        return dto;
    }
}


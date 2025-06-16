package com.nova.deliveries.repository;

import com.nova.deliveries.entity.Delivery;
import com.nova.deliveries.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    // Buscar entregas por ID de orden
    List<Delivery> findByOrdenId(Long ordenId);

    // Buscar entregas por ID de repartidor
    List<Delivery> findByRepartidorId(Long repartidorId);

    // Buscar entregas disponibles (sin repartidor asignado)
    List<Delivery> findByRepartidorIdIsNull();

    // Buscar entregas por estado
    List<Delivery> findByEstado(DeliveryStatus estado);

    // Buscar entregas por repartidor y estado
    List<Delivery> findByRepartidorIdAndEstado(Long repartidorId, DeliveryStatus estado);
}
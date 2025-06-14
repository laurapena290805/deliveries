package com.nova.deliveries.repository;

import com.nova.deliveries.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByOrdenId(Long ordenId);
    List<Delivery> findByRepartidorId(Long repartidorId);
    List<Delivery> findByRepartidorIdAndAsignacionPendiente(Long repartidorId, boolean asignacionPendiente);
    List<Delivery> findByAsignacionPendiente(boolean asignacionPendiente);
}


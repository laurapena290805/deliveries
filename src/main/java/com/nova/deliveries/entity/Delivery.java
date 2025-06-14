package com.nova.deliveries.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ordenId;
    private String direccion;
    private LocalDate fechaEstimada;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus estado;

    private Long repartidorId; // ID del usuario repartidor
    private boolean asignacionPendiente; // true si está esperando aprobación
}
package com.nova.deliveries.dto;

import com.nova.deliveries.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {
    private Long id;
    private Long ordenId;
    private String direccion;
    private LocalDate fechaEstimada;
    private DeliveryStatus estado;
    private Long repartidorId; // Nuevo campo
    private boolean asignacionPendiente; // Nuevo campo
    private String nombreRepartidor; // Podría obtenerse de un servicio de usuarios
}


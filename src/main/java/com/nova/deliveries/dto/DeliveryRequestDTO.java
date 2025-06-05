package com.nova.deliveries.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Data
public class DeliveryRequestDTO {
    @NotNull
    private Long ordenId;
    private String direccion;
    private LocalDate fechaEstimada;
}


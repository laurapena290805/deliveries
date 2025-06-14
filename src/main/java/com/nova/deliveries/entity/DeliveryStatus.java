package com.nova.deliveries.entity;

// DeliveryStatus.java
public enum DeliveryStatus {
    PENDIENTE,
    PENDIENTE_APROBACION, // Nuevo estado
    EN_CAMINO,
    ENTREGADA,
    CANCELADA,
    RECHAZADA // Para cuando el admin rechaza una asignación
}


package com.nova.deliveries.dto;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
}
package com.nova.deliveries.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.deliveries.dto.DeliveryResponseDTO;
import com.nova.deliveries.entity.DeliveryStatus;
import com.nova.deliveries.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryController.class)
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearEntrega_shouldReturnCreatedDelivery() throws Exception {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("ordenId", 1L);
        requestMap.put("direccion", "123 Test Street");
        requestMap.put("fechaEstimada", "2024-01-01");

        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOrdenId(1L);
        responseDTO.setDireccion("123 Test Street");
        responseDTO.setFechaEstimada(LocalDate.parse("2024-01-01"));
        responseDTO.setEstado(DeliveryStatus.PENDIENTE);

        when(deliveryService.crearEntrega(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/entregas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ordenId").value(1L))
                .andExpect(jsonPath("$.direccion").value("123 Test Street"))
                .andExpect(jsonPath("$.fechaEstimada").value("2024-01-01"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }
} 
package com.nova.deliveries.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.deliveries.dto.DeliveryResponseDTO;
import com.nova.deliveries.entity.DeliveryStatus;
import com.nova.deliveries.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeliveryControllerTest2 {

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

    @Test
    void cambiarEstado_shouldReturnUpdatedDelivery() throws Exception {
        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEstado(DeliveryStatus.EN_CAMINO);

        when(deliveryService.cambiarEstado(1L, DeliveryStatus.EN_CAMINO)).thenReturn(responseDTO);

        mockMvc.perform(patch("/entregas/{id}/estado", 1L)
                        .param("estado", "EN_CAMINO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("EN_CAMINO"));
    }

    @Test
    void obtenerPorOrden_shouldReturnDeliveryList() throws Exception {
        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setOrdenId(1L);

        when(deliveryService.obtenerPorOrden(1L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/entregas/orden/{ordenId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].ordenId").value(1L));
    }

    @Test
    void listarTodasLasEntregas_shouldReturnDeliveryList() throws Exception {
        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);

        when(deliveryService.listarTodasLasEntregas()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/entregas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void asignarRepartidor_shouldReturnUpdatedDelivery() throws Exception {
        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRepartidorId(10L);

        when(deliveryService.asignarRepartidor(1L, 10L)).thenReturn(responseDTO);

        mockMvc.perform(post("/entregas/{id}/asignar-repartidor/{repartidorId}", 1L, 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.repartidorId").value(10L));
    }

    @Test
    void crearEntrega_withInvalidData_shouldReturnBadRequest() throws Exception {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("ordenId", "not-a-number");
        requestMap.put("direccion", "123 Test Street");
        requestMap.put("fechaEstimada", "2024-01-01");

        mockMvc.perform(post("/entregas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarEstado_whenDeliveryNotFound_shouldReturnNotFound() throws Exception {
        when(deliveryService.cambiarEstado(999L, DeliveryStatus.EN_CAMINO))
                .thenThrow(new RuntimeException("Delivery not found"));

        mockMvc.perform(patch("/entregas/{id}/estado", 999L)
                        .param("estado", "EN_CAMINO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void escogerEntrega_shouldReturnUpdatedDelivery() throws Exception {
        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRepartidorId(10L);
        responseDTO.setEstado(DeliveryStatus.EN_CAMINO);

        when(deliveryService.escogerEntrega(10L, 1L)).thenReturn(responseDTO);

        mockMvc.perform(post("/entregas/{id}/escoger-entrega/{repartidorId}", 1L, 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.repartidorId").value(10L))
                .andExpect(jsonPath("$.estado").value("EN_CAMINO"));
    }

    @Test
    void obtenerEntregasDisponibles_shouldReturnAvailableDeliveries() throws Exception {
        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setEstado(DeliveryStatus.PENDIENTE);

        when(deliveryService.obtenerEntregasDisponibles()).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/entregas/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void obtenerEntregasPorRepartidor_shouldReturnDeliveriesForRepartidor() throws Exception {
        DeliveryResponseDTO responseDTO = new DeliveryResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRepartidorId(10L);

        when(deliveryService.obtenerEntregasPorRepartidor(10L)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/entregas/repartidor/{repartidorId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].repartidorId").value(10L));
    }
} 
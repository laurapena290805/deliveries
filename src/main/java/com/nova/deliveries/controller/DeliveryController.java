package com.nova.deliveries.controller;

import com.nova.deliveries.dto.DeliveryRequestDTO;
import com.nova.deliveries.dto.DeliveryResponseDTO;
import com.nova.deliveries.entity.Delivery;
import com.nova.deliveries.entity.DeliveryStatus;
import com.nova.deliveries.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entregas")
public class DeliveryController {

    @Autowired
    private DeliveryService entregaService;

    @PostMapping
    public ResponseEntity<?> crearEntrega(@RequestBody Map<String, Object> requestMap) {
        try {
            Long ordenId = Long.parseLong(requestMap.get("ordenId").toString());
            String direccion = (String) requestMap.get("direccion");
            LocalDate fechaEstimada = LocalDate.parse(requestMap.get("fechaEstimada").toString());

            DeliveryRequestDTO dto = new DeliveryRequestDTO();
            dto.setOrdenId(ordenId);
            dto.setDireccion(direccion);
            dto.setFechaEstimada(fechaEstimada);

            return new ResponseEntity<>(entregaService.crearEntrega(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de datos inválido");
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DeliveryResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam DeliveryStatus estado
    ) {
        return ResponseEntity.ok(entregaService.cambiarEstado(id, estado));
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<DeliveryResponseDTO>> obtenerPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(entregaService.obtenerPorOrden(ordenId));
    }

    // Listar todas las entregas
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> listarTodasLasEntregas() {
        return ResponseEntity.ok(entregaService.listarTodasLasEntregas());
    }
}


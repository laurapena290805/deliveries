package com.nova.deliveries.controller;

import com.nova.deliveries.dto.DeliveryRequestDTO;
import com.nova.deliveries.dto.DeliveryResponseDTO;
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
    private DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> crearEntrega(@RequestBody Map<String, Object> requestMap) {
        try {
            System.out.println("➡️ Request recibido: " + requestMap);

            Long ordenId = Long.parseLong(requestMap.get("ordenId").toString());
            String direccion = (String) requestMap.get("direccion");
            String fechaRaw = requestMap.get("fechaEstimada").toString();
            System.out.println("📅 Fecha recibida: " + fechaRaw);

            LocalDate fechaEstimada = LocalDate.parse(fechaRaw);

            DeliveryRequestDTO dto = new DeliveryRequestDTO();
            dto.setOrdenId(ordenId);
            dto.setDireccion(direccion);
            dto.setFechaEstimada(fechaEstimada);

            return new ResponseEntity<>(deliveryService.crearEntrega(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Formato de datos inválido: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam DeliveryStatus estado) {
        try {
            return ResponseEntity.ok(deliveryService.cambiarEstado(id, estado));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<DeliveryResponseDTO>> obtenerPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(deliveryService.obtenerPorOrden(ordenId));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> listarTodasLasEntregas() {
        return ResponseEntity.ok(deliveryService.listarTodasLasEntregas());
    }

    // Asignar repartidor (para administradores)
    @PostMapping("/{id}/asignar-repartidor/{repartidorId}")
    public ResponseEntity<DeliveryResponseDTO> asignarRepartidor(
            @PathVariable Long id,
            @PathVariable Long repartidorId) {
        return ResponseEntity.ok(deliveryService.asignarRepartidor(id, repartidorId));
    }

    // Escoger entrega (para repartidores)
    @PostMapping("/{id}/escoger-entrega/{repartidorId}")
    public ResponseEntity<DeliveryResponseDTO> escogerEntrega(
            @PathVariable Long id,
            @PathVariable Long repartidorId) {
        return ResponseEntity.ok(deliveryService.escogerEntrega(repartidorId, id));
    }

    // Obtener entregas disponibles (para repartidores)
    @GetMapping("/disponibles")
    public ResponseEntity<List<DeliveryResponseDTO>> obtenerEntregasDisponibles() {
        return ResponseEntity.ok(deliveryService.obtenerEntregasDisponibles());
    }

    // Obtener entregas de un repartidor
    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<DeliveryResponseDTO>> obtenerEntregasPorRepartidor(
            @PathVariable Long repartidorId) {
        return ResponseEntity.ok(deliveryService.obtenerEntregasPorRepartidor(repartidorId));
    }
}
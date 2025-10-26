package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleVentas;
import sigiv.Backend.sigiv.Backend.repository.DetalleVentaRepository;
import sigiv.Backend.sigiv.Backend.services.DetalleVentasService;

@RestController
@RequestMapping("/api/detalleventas")
@CrossOrigin(origins = "*")
public class DetalleVentasController {

    @Autowired
    private DetalleVentasService detalleVentasService;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    // 📦 Agregar producto a una venta
    @PostMapping("/agregar/{ventaId}")
    public ResponseEntity<DetalleVentaResponseDto> agregarDetalle(
            @PathVariable Long ventaId,
            @RequestBody DetalleVentaRequestDto dto
    ) {
        DetalleVentaResponseDto detalle = detalleVentasService.agregarDetalleAVenta(ventaId, dto);
        return ResponseEntity.ok(detalle);
    }

    // 📋 Obtener todos los detalles
    @GetMapping
    public ResponseEntity<List<DetalleVentas>> obtenerTodos() {
        return ResponseEntity.ok(detalleVentaRepository.findAll());
    }

    // 🔍 Obtener detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentas> obtenerPorId(@PathVariable Long id) {
        return detalleVentaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ❌ Eliminar detalle (reponer stock y actualizar venta)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        detalleVentasService.eliminarDetalle(id);
        return ResponseEntity.noContent().build();
    }
     @DeleteMapping("/venta/{ventaId}")
    public ResponseEntity<?> eliminarDetallesPorVenta(@PathVariable Long ventaId) {
        try {
            detalleVentasService.eliminarTodosLosDetallesDeVenta(ventaId);
            return ResponseEntity.ok().body(
                    java.util.Map.of("message", "Todos los detalles de la venta con ID " + ventaId + " fueron eliminados correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    java.util.Map.of("error", e.getMessage())
            );
        }
    }
}

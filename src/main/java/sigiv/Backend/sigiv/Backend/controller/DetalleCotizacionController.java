package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;
import sigiv.Backend.sigiv.Backend.repository.DetalleCotizacionRepository;
import sigiv.Backend.sigiv.Backend.services.DetalleCotizacionService;

@RestController
@RequestMapping("/api/detallecotizacion")
@CrossOrigin(origins = "*")
public class DetalleCotizacionController {

    @Autowired
    private DetalleCotizacionService detalleCotizacionService;

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @PostMapping("/agregar/{cotizacionId}")
    public ResponseEntity<DetalleCotizacionResponseDto> agregarDetalle(
            @PathVariable Long cotizacionId,
            @RequestBody DetalleCotizacionRequestDto dto
    ) {
        DetalleCotizacionResponseDto detalle = detalleCotizacionService.agregarDetalleACotizacion(cotizacionId, dto);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping
    public ResponseEntity<List<DetalleCotizacion>> obtenerTodos() {
        return ResponseEntity.ok(detalleCotizacionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCotizacion> obtenerPorId(@PathVariable Long id) {
        return detalleCotizacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Long id) {
        detalleCotizacionService.eliminarDetalle(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cotizacion/{cotizacionId}")
    public ResponseEntity<?> eliminarDetallesPorCotizacion(@PathVariable Long cotizacionId) {
        try {
            detalleCotizacionService.eliminarTodosLosDetallesDeCotizacion(cotizacionId);
            return ResponseEntity.ok().body(
                    java.util.Map.of("message", "Todos los detalles de la cotización con ID " + cotizacionId + " fueron eliminados correctamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    java.util.Map.of("error", e.getMessage())
            );
        }
    }
}

package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;
import sigiv.Backend.sigiv.Backend.repository.DetalleCotizacionRepository;

@RestController
@RequestMapping("/detalle-cotizacion")
@CrossOrigin(origins = "*")
public class DetalleCotizacionController {

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    // 🔹 Obtener todos los detalles
    @GetMapping
    public List<DetalleCotizacion> getAllDetalles() {
        return detalleCotizacionRepository.findAll();
    }

    // 🔹 Obtener un detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleCotizacion> getDetalleById(@PathVariable Long id) {
        Optional<DetalleCotizacion> detalle = detalleCotizacionRepository.findById(id);
        return detalle.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Crear un nuevo detalle
    @PostMapping
    public DetalleCotizacion createDetalle(@RequestBody DetalleCotizacion detalle) {
        return detalleCotizacionRepository.save(detalle);
    }

    // 🔹 Actualizar un detalle existente
    @PutMapping("/{id}")
    public ResponseEntity<DetalleCotizacion> updateDetalle(@PathVariable Long id, @RequestBody DetalleCotizacion detalleRequest) {
        return detalleCotizacionRepository.findById(id).map(detalle -> {
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecio(detalleRequest.getPrecio());
            detalle.setDescripcionProducto(detalleRequest.getDescripcionProducto());
            detalle.setCotizacion(detalleRequest.getCotizacion());
            detalle.setProducto(detalleRequest.getProducto());
            return ResponseEntity.ok(detalleCotizacionRepository.save(detalle));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Eliminar un detalle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalle(@PathVariable Long id) {
        if (detalleCotizacionRepository.existsById(id)) {
            detalleCotizacionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 🔹 Obtener todos los detalles de una cotización específica
    @GetMapping("/cotizacion/{cotizacionId}")
    public List<DetalleCotizacion> getDetallesByCotizacion(@PathVariable Long cotizacionId) {
        return detalleCotizacionRepository.findByCotizacion_Idcotizacion(cotizacionId);
    }
}

package sigiv.Backend.sigiv.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigiv.Backend.sigiv.Backend.entity.Cotizacion;
import sigiv.Backend.sigiv.Backend.repository.CotizacionRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cotizaciones")
public class CotizacionController {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    // 🔹 Obtener todas las cotizaciones
    @GetMapping
    public List<Cotizacion> getAllCotizaciones() {
        return cotizacionRepository.findAll();
    }

    // 🔹 Obtener una cotización por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cotizacion> getCotizacionById(@PathVariable Long id) {
        Optional<Cotizacion> cotizacion = cotizacionRepository.findById(id);
        return cotizacion.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Crear una nueva cotización
    @PostMapping
    public Cotizacion createCotizacion(@RequestBody Cotizacion cotizacion) {
        return cotizacionRepository.save(cotizacion);
    }

    // 🔹 Actualizar una cotización existente
    @PutMapping("/{id}")
    public ResponseEntity<Cotizacion> updateCotizacion(@PathVariable Long id, @RequestBody Cotizacion cotizacionDetails) {
        return cotizacionRepository.findById(id)
                .map(cotizacion -> {
                    cotizacion.setNombreCliente(cotizacionDetails.getNombreCliente());
                    cotizacion.setTelefonoCliente(cotizacionDetails.getTelefonoCliente());
                    cotizacion.setFecha(cotizacionDetails.getFecha());
                    cotizacion.setTotal(cotizacionDetails.getTotal());
                    cotizacion.setUsuario(cotizacionDetails.getUsuario());
                    Cotizacion updated = cotizacionRepository.save(cotizacion);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Eliminar una cotización
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCotizacion(@PathVariable Long id) {
        return cotizacionRepository.findById(id)
                .map(cotizacion -> {
                    cotizacionRepository.delete(cotizacion);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

  
}

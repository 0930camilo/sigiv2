package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.DetalleVentas;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Ventas;
import sigiv.Backend.sigiv.Backend.repository.DetalleVentaRepository;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.repository.VentasRepository;

@RestController
@RequestMapping("/detalleventas")
public class DetalleVentasController {

    @Autowired
    private DetalleVentaRepository detalleVentasRepository;

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener todos los detalles de venta
    @GetMapping
    public ResponseEntity<List<DetalleVentas>> obtenerTodos() {
        return ResponseEntity.ok(detalleVentasRepository.findAll());
    }

    // Obtener detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentas> obtenerPorId(@PathVariable Long id) {
        return detalleVentasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Registrar un nuevo detalle de venta
    @PostMapping
    public ResponseEntity<DetalleVentas> registrarDetalle(@RequestBody DetalleVentas detalle) {
        Optional<Ventas> ventaOpt = ventasRepository.findById(detalle.getVenta().getIdventa());
        if (!ventaOpt.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        Optional<Producto> productoOpt = productoRepository.findById(detalle.getProducto().getIdproducto());
        if (!productoOpt.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        detalle.setVenta(ventaOpt.get());
        detalle.setProducto(productoOpt.get());

        DetalleVentas guardado = detalleVentasRepository.save(detalle);
        return ResponseEntity.ok(guardado);
    }

    // Actualizar detalle existente
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVentas> actualizar(
            @PathVariable Long id,
            @RequestBody DetalleVentas detalleActualizado
    ) {
        return detalleVentasRepository.findById(id)
                .map(detalle -> {
                    detalle.setCantidad(detalleActualizado.getCantidad());
                    detalle.setPrecio(detalleActualizado.getPrecio());
                    detalle.setDescripcionProducto(detalleActualizado.getDescripcionProducto());
                    detalle.setVenta(detalleActualizado.getVenta());
                    detalle.setProducto(detalleActualizado.getProducto());
                    DetalleVentas actualizado = detalleVentasRepository.save(detalle);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar detalle físicamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (detalleVentasRepository.existsById(id)) {
            detalleVentasRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

package sigiv.Backend.sigiv.Backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.entity.Ventas;
import sigiv.Backend.sigiv.Backend.repository.UsuarioRepository;
import sigiv.Backend.sigiv.Backend.repository.VentasRepository;

@RestController
@RequestMapping("/ventas")
public class VentasController {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todas las ventas
    @GetMapping
    public ResponseEntity<List<Ventas>> obtenerTodas() {
        return ResponseEntity.ok(ventasRepository.findAll());
    }

    // Obtener venta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Ventas> obtenerPorId(@PathVariable Long id) {
        return ventasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Guardar nueva venta
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Ventas nuevaVenta) {
        if (nuevaVenta.getUsuario() == null || nuevaVenta.getUsuario().getIdUsuario() == 0) {
            return ResponseEntity.badRequest().body("El ID del usuario es obligatorio");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(nuevaVenta.getUsuario().getIdUsuario());
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        nuevaVenta.setUsuario(usuarioOpt.get());
        nuevaVenta.setFecha(LocalDateTime.now());

        Ventas guardada = ventasRepository.save(nuevaVenta);
        return ResponseEntity.ok(guardada);
    }

    // Actualizar venta existente
    @PutMapping("/{id}")
    public ResponseEntity<Ventas> actualizar(@PathVariable Long id, @RequestBody Ventas ventaActualizada) {
        return ventasRepository.findById(id)
                .map(venta -> {
                    venta.setFecha(ventaActualizada.getFecha());
                    venta.setTotal(ventaActualizada.getTotal());
                    venta.setNombreCliente(ventaActualizada.getNombreCliente());
                    venta.setTelefonoCliente(ventaActualizada.getTelefonoCliente());
                    venta.setEfectivo(ventaActualizada.getEfectivo());
                    venta.setCambio(ventaActualizada.getCambio());

                    if (ventaActualizada.getUsuario() != null && ventaActualizada.getUsuario().getIdUsuario() != 0) {
                        usuarioRepository.findById(ventaActualizada.getUsuario().getIdUsuario())
                                .ifPresent(venta::setUsuario);
                    }

                    Ventas actualizado = ventasRepository.save(venta);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar venta físicamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (ventasRepository.existsById(id)) {
            ventasRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

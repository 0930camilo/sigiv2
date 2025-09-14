package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.repository.ProveedorRepository;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    // Obtener todos los proveedores
    @GetMapping
    public ResponseEntity<List<Proveedor>> obtenerTodos() {
        return ResponseEntity.ok(proveedorRepository.findAll());
    }

    // Obtener proveedores activos
    @GetMapping("/activos")
    public ResponseEntity<List<Proveedor>> obtenerActivos() {
        return ResponseEntity.ok(proveedorRepository.findByEstado(Proveedor.Estado.Activo));
    }

    // Obtener proveedores inactivos
    @GetMapping("/inactivos")
    public ResponseEntity<List<Proveedor>> obtenerInactivos() {
        return ResponseEntity.ok(proveedorRepository.findByEstado(Proveedor.Estado.Inactivo));
    }

    // Obtener proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Guardar nuevo proveedor
    @PostMapping
    public ResponseEntity<Proveedor> guardar(@RequestBody Proveedor nuevoProveedor) {
        Proveedor guardado = proveedorRepository.save(nuevoProveedor);
        return ResponseEntity.ok(guardado);
    }

    // Actualizar proveedor
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @RequestBody Proveedor proveedorActualizado) {
        return proveedorRepository.findById(id)
                .map(proveedor -> {
                    proveedor.setNombre(proveedorActualizado.getNombre());
                    proveedor.setTelefono(proveedorActualizado.getTelefono());
                    proveedor.setEstado(proveedorActualizado.getEstado());
                    proveedor.setUsuario(proveedorActualizado.getUsuario());
                    Proveedor actualizado = proveedorRepository.save(proveedor);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar físicamente un proveedor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminación lógica (estado = Inactivo)
    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Proveedor> eliminarLogicamente(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .map(proveedor -> {
                    proveedor.setEstado(Proveedor.Estado.Inactivo);
                    Proveedor actualizado = proveedorRepository.save(proveedor);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener todos los productos de un proveedor
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<Producto>> obtenerProductosPorProveedor(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .map(proveedor -> ResponseEntity.ok(proveedor.getProductos()))
                .orElse(ResponseEntity.notFound().build());
    }
}

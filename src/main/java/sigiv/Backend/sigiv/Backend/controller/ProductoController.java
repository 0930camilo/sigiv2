package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.Producto;

import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener todos los productos
    @GetMapping("/mostrar")
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Obtener productos activos
    @GetMapping("/activos")
    public List<Producto> obtenerActivos() {
        return productoRepository.findByEstado(Producto.Estado.Activo);
    }

    // Obtener productos inactivos
    @GetMapping("/inactivos")
    public List<Producto> obtenerInactivos() {
        return productoRepository.findByEstado(Producto.Estado.Inactivo);
    }

    // Buscar producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Guardar nuevo producto
    @PostMapping("/guardar")
    public Producto guardar(@RequestBody Producto nuevoProducto) {
        return productoRepository.save(nuevoProducto);
    }

    // Actualizar producto por ID
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setDescripcion(productoActualizado.getDescripcion());
                    producto.setCantidad(productoActualizado.getCantidad());
                    producto.setPrecio(productoActualizado.getPrecio());
                    producto.setPrecioCompra(productoActualizado.getPrecioCompra());
                    producto.setFecha(productoActualizado.getFecha());
                    producto.setEstado(productoActualizado.getEstado());
                    producto.setUsuario(productoActualizado.getUsuario());
                    producto.setCategoria(productoActualizado.getCategoria());
                    producto.setProveedor(productoActualizado.getProveedor());
                    Producto actualizado = productoRepository.save(producto);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar (físicamente) producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar lógicamente (estado = Inactivo)
    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Producto> eliminarLogico(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setEstado(Producto.Estado.Inactivo);
                    Producto actualizado = productoRepository.save(producto);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    

}

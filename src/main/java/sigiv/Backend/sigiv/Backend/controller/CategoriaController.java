package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.repository.CategoriaRepository;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Obtener todas las categorías
    @GetMapping("/mostrar")
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    // Obtener categorías activas
    @GetMapping("/activas")
    public List<Categoria> obtenerActivas() {
        return categoriaRepository.findByEstado(Categoria.Estado.Activo);
    }

    // Obtener categorías inactivas
    @GetMapping("/inactivas")
    public List<Categoria> obtenerInactivas() {
        return categoriaRepository.findByEstado(Categoria.Estado.Inactivo);
    }

    // Obtener categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Guardar nueva categoría
    @PostMapping("/guardar")
    public Categoria guardar(@RequestBody Categoria nuevaCategoria) {
        return categoriaRepository.save(nuevaCategoria);
    }

    // Actualizar categoría existente
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria categoriaActualizada) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNombre(categoriaActualizada.getNombre());
                    categoria.setEstado(categoriaActualizada.getEstado());
                    categoria.setUsuario(categoriaActualizada.getUsuario());
                    Categoria actualizada = categoriaRepository.save(categoria);
                    return ResponseEntity.ok(actualizada);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar categoría (físicamente)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminación lógica (cambiar estado a Inactivo)
    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Categoria> eliminarLogicamente(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setEstado(Categoria.Estado.Inactivo);
                    Categoria actualizada = categoriaRepository.save(categoria);
                    return ResponseEntity.ok(actualizada);
                }).orElse(ResponseEntity.notFound().build());
    }

      @GetMapping("/{id}/productos")
public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable Long id) {
    Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);

    if (categoriaOptional.isPresent()) {
        Categoria categoria = categoriaOptional.get();
        List<Producto> productos = categoria.getProductos();
        return ResponseEntity.ok(productos);
    } else {
        return ResponseEntity.notFound().build();
    }
}
}

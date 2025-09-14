package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.repository.UsuarioRepository;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // Obtener usuarios activos
    @GetMapping("/activos")
    public ResponseEntity<List<Usuario>> obtenerActivos() {
        return ResponseEntity.ok(usuarioRepository.findByEstado(Usuario.Estado.Activo));
    }

    // Obtener usuarios inactivos
    @GetMapping("/inactivos")
    public ResponseEntity<List<Usuario>> obtenerInactivos() {
        return ResponseEntity.ok(usuarioRepository.findByEstado(Usuario.Estado.Inactivo));
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Guardar nuevo usuario
    @PostMapping
    public ResponseEntity<Usuario> guardar(@RequestBody Usuario nuevoUsuario) {
        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok(guardado);
    }

    // Actualizar usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuarioActualizado
    ) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombres(usuarioActualizado.getNombres());
                    usuario.setClave(usuarioActualizado.getClave());
                    usuario.setTelefono(usuarioActualizado.getTelefono());
                    usuario.setDireccion(usuarioActualizado.getDireccion());
                    usuario.setEstado(usuarioActualizado.getEstado());
                    Usuario actualizado = usuarioRepository.save(usuario);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar usuario físicamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar usuario por estado (eliminación lógica)
    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Usuario> eliminarLogicamente(@PathVariable Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setEstado(Usuario.Estado.Inactivo);
            Usuario actualizado = usuarioRepository.save(usuario);
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

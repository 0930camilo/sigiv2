package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioRequestDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.services.UsuarioService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/crear-usuarios")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> crear(@RequestBody UsuarioRequestDto dto) {
        UsuarioResponseDto created = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Usuario creado correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDto usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Usuario encontrado", usuario)
        );
    }

    @GetMapping("/list-users")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listar() {
        List<UsuarioResponseDto> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todos los usuarios listados", usuarios)
        );
    }

    @GetMapping("/list-user-status")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Usuario.Estado estado) {

        List<UsuarioResponseDto> usuarios;
        String message;

        if (estado != null) {
            usuarios = usuarioService.listarPorEstado(estado);
            message = "Usuarios listados por estado: " + estado;
        } else {
            usuarios = usuarioService.listarUsuarios();
            message = "Todos los usuarios listados";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, usuarios)
        );
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDto dto) {
        UsuarioResponseDto actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Usuario actualizado correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Usuario eliminado correctamente", null)
        );
    }

 @PutMapping("/cambiar-estado/{id}")
public ResponseEntity<ApiResponse<UsuarioResponseDto>> cambiarEstado(@PathVariable Long id) {
    UsuarioResponseDto actualizado = usuarioService.cambiarEstado(id);
    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Estado del usuario actualizado automáticamente", actualizado)
    );
}
}

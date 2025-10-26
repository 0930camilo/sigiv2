package sigiv.Backend.sigiv.Backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioRequestDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
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
                        "Usuario creado correctamente", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDto usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Usuario encontrado", usuario));
    }

    @GetMapping("/list-users")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listar() {
        List<UsuarioResponseDto> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todos los usuarios listados", usuarios));
    }

    @GetMapping("/empresa/{empresaId}/list-users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UsuarioResponseDto> usuariosPage = usuarioService.listarUsuariosPorEmpresa(empresaId, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("usuarios", usuariosPage.getContent());
        data.put("totalElements", usuariosPage.getTotalElements());
        data.put("totalPages", usuariosPage.getTotalPages());
        data.put("currentPage", usuariosPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Usuarios de la empresa " + empresaId + " listados correctamente",
                        data));
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
                new ApiResponse<>(true, HttpStatus.OK.value(), message, usuarios));
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDto dto) {
        UsuarioResponseDto actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Usuario actualizado correctamente", actualizado));
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Usuario eliminado correctamente", null));
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> cambiarEstado(@PathVariable Long id) {
        UsuarioResponseDto actualizado = usuarioService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado del usuario actualizado automáticamente", actualizado));
    }


@GetMapping("/{id}/total-vendido")
public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorUsuario(@PathVariable Long id) {
    BigDecimal total = usuarioService.calcularTotalVendido(id);
    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    "Total vendido por el usuario con id " + id, total)
    );
}


@GetMapping("/{id}/total-vendido-rango")
public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorUsuarioEntreFechas(
        @PathVariable Long id,
        @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

    BigDecimal total = usuarioService.calcularTotalVendidoEntreFechas(id, fechaInicio, fechaFin);

    return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(),
                    String.format("Total vendido por el usuario con id %d entre %s y %s", id, fechaInicio, fechaFin),
                    total)
    );
}
@GetMapping("/ganancia/usuario/{idUsuario}")
public ResponseEntity<ApiResponse<BigDecimal>> obtenerGananciaPorUsuario(@PathVariable Long idUsuario) {
    BigDecimal ganancia = usuarioService.calcularGananciaPorUsuario(idUsuario);
    return ResponseEntity.ok(
        new ApiResponse<>(true, HttpStatus.OK.value(),
                "Ganancia obtenida correctamente para el usuario con id " + idUsuario, ganancia)
    );
}
@GetMapping("/{idUsuario}/categorias")
public ResponseEntity<ApiResponse<List<Categoria>>> listarCategoriasPorUsuario(@PathVariable Long idUsuario) {
    List<Categoria> categorias = usuarioService.listarCategoriasPorUsuario(idUsuario);

    return ResponseEntity.ok(
        new ApiResponse<>(
            true,
            HttpStatus.OK.value(),
            "Categorías obtenidas correctamente",
            categorias
        )
    );
}


@GetMapping("/{idUsuario}/proveedores")
public ResponseEntity<ApiResponse<List<sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto>>> listarProveedoresPorUsuario(
        @PathVariable Long idUsuario) {

    List<sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto> proveedores = usuarioService.listarProveedoresPorUsuario(idUsuario);

    return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Proveedores obtenidos correctamente para el usuario con id " + idUsuario,
                    proveedores
            )
    );
}

}

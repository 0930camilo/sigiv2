package sigiv.Backend.sigiv.Backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioRequestDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.services.UsuarioService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    private Long getEmpresaIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extraerClaims(token);
        // Tokens de empresa traen "id", tokens de usuario traen "empresa_id"
        Long empresaId = claims.get("empresa_id", Long.class);
        if (empresaId != null) return empresaId;
        return claims.get("id", Long.class);
    }

    private ResponseEntity<ApiResponse> checkPermissions(Long requiredEmpresaId, HttpServletRequest request) {
        Long tokenEmpresaId = getEmpresaIdFromToken(request);
        if (tokenEmpresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null)
            );
        }
        if (!tokenEmpresaId.equals(requiredEmpresaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(false, HttpStatus.FORBIDDEN.value(), "No tienes permiso para acceder a este recurso", null)
            );
        }
        return null; // All good
    }

    @PostMapping("/crear-usuarios")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> crear(@RequestBody UsuarioRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(dto.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        UsuarioResponseDto created = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "Usuario creado correctamente", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        UsuarioResponseDto usuario = usuarioService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(usuario.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Usuario encontrado", usuario));
    }

    @GetMapping("/empresa/{empresaId}/list-users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Usuario.Estado estado,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String documento,
            HttpServletRequest request) {

        ResponseEntity<ApiResponse> errorResponse = checkPermissions(empresaId, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        Page<UsuarioResponseDto> usuariosPage = usuarioService.listarUsuariosPorEmpresa(empresaId, page, size, estado, nombres, documento);
        Map<String, Object> data = new HashMap<>();
        data.put("usuarios", usuariosPage.getContent());
        data.put("totalElements", usuariosPage.getTotalElements());
        data.put("totalPages", usuariosPage.getTotalPages());
        data.put("currentPage", usuariosPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Usuarios de la empresa " + empresaId + " listados correctamente", data));
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDto>> actualizar(
            @PathVariable Long id, @RequestBody UsuarioRequestDto dto, HttpServletRequest request) {
        UsuarioResponseDto usuarioExistente = usuarioService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(usuarioExistente.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        UsuarioResponseDto actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Usuario actualizado correctamente", actualizado));
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id, HttpServletRequest request) {
        UsuarioResponseDto usuario = usuarioService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(usuario.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Usuario eliminado correctamente", null));
    }

    @GetMapping("/{id}/total-vendido")
    public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorUsuario(@PathVariable Long id, HttpServletRequest request) {
        UsuarioResponseDto usuario = usuarioService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(usuario.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        BigDecimal total = usuarioService.calcularTotalVendido(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Total vendido por el usuario con id " + id, total));
    }

    @GetMapping("/{id}/total-vendido-rango")
    public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorUsuarioEntreFechas(
            @PathVariable Long id,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletRequest request) {
        
        UsuarioResponseDto usuario = usuarioService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(usuario.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        BigDecimal total = usuarioService.calcularTotalVendidoEntreFechas(id, fechaInicio, fechaFin);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), String.format("Total vendido por el usuario con id %d entre %s y %s", id, fechaInicio, fechaFin), total));
    }

    @GetMapping("/ganancia/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<BigDecimal>> obtenerGananciaPorUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletRequest request) {

        UsuarioResponseDto usuario = usuarioService.obtenerPorId(idUsuario);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(usuario.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        BigDecimal ganancia;
        if (fechaInicio != null && fechaFin != null) {
            ganancia = usuarioService.calcularGananciaPorUsuarioEntreFechas(idUsuario, fechaInicio, fechaFin);
        } else {
            ganancia = usuarioService.calcularGananciaPorUsuario(idUsuario);
        }
        return ResponseEntity.ok(
            new ApiResponse<>(true, HttpStatus.OK.value(), "Ganancia obtenida correctamente para el usuario con id " + idUsuario, ganancia));
    }
}
package sigiv.Backend.sigiv.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorRequestDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.services.ProveedorService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final JwtUtil jwtUtil;

    private Long getEmpresaIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extraerClaims(token);
        Long empresaId = claims.get("empresa_id", Long.class);
        if (empresaId != null) return empresaId;
        return claims.get("id", Long.class);
    }

    private ResponseEntity<ApiResponse<?>> checkPermissions(Long requiredEmpresaId, HttpServletRequest request) {
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

    @PostMapping("/crear-proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponseDto>> crear(@RequestBody ProveedorRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(dto.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<ProveedorResponseDto>) errorResponse.getBody());

        ProveedorResponseDto created = proveedorService.crearProveedor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "Proveedor creado correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDto>> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        ProveedorResponseDto proveedor = proveedorService.obtenerPorId(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(proveedor.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<ProveedorResponseDto>) errorResponse.getBody());

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Proveedor encontrado", proveedor)
        );
    }

    @GetMapping("/list-proveedores")
    public ResponseEntity<ApiResponse<List<ProveedorResponseDto>>> listar(HttpServletRequest request) {
        Long empresaId = getEmpresaIdFromToken(request);
        if (empresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null));
        }
        List<ProveedorResponseDto> proveedores = proveedorService.listarProveedoresPorEmpresa(empresaId, 0, Integer.MAX_VALUE, null, null, null).getContent();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Todos los proveedores listados", proveedores)
        );
    }

    @PutMapping("/update-proveedor/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDto>> actualizar(
            @PathVariable Long id, @RequestBody ProveedorRequestDto dto, HttpServletRequest request) {
        ProveedorResponseDto proveedorExistente = proveedorService.obtenerPorId(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(proveedorExistente.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<ProveedorResponseDto>) errorResponse.getBody());

        ProveedorResponseDto actualizado = proveedorService.actualizarProveedor(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Proveedor actualizado correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-proveedor/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id, HttpServletRequest request) {
        ProveedorResponseDto proveedor = proveedorService.obtenerPorId(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(proveedor.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<Void>) errorResponse.getBody());

        proveedorService.eliminarProveedor(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Proveedor eliminado correctamente", null)
        );
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Proveedor.Estado estado,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String documento,
            HttpServletRequest request
    ) {
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(empresaId, request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<Map<String, Object>>) errorResponse.getBody());

        Page<ProveedorResponseDto> proveedoresPage =
                proveedorService.listarProveedoresPorEmpresa(
                        empresaId,
                        page,
                        size,
                        estado,
                        nombre,
                        documento
                );

        Map<String, Object> data = new HashMap<>();
        data.put("proveedores", proveedoresPage.getContent());
        data.put("totalElements", proveedoresPage.getTotalElements());
        data.put("totalPages", proveedoresPage.getTotalPages());
        data.put("currentPage", proveedoresPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Proveedores de la empresa " + empresaId + " listados correctamente",
                        data
                )
        );
    }
}
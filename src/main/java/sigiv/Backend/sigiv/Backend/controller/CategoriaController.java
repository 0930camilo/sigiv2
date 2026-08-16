package sigiv.Backend.sigiv.Backend.controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.services.CategoriaService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;
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

    @PostMapping("/crear-categoria")
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> crear(@RequestBody CategoriaRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(dto.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        CategoriaResponseDto created = categoriaService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "Categoria creada correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Categoria encontrada", categoria)
        );
    }

    @PutMapping("/update-categoria/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDto>> actualizar(
            @PathVariable Long id, @RequestBody CategoriaRequestDto dto, HttpServletRequest request) {
        CategoriaResponseDto categoriaExistente = categoriaService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoriaExistente.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        CategoriaResponseDto actualizado = categoriaService.actualizarCategoria(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Categoria actualizada correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-categoria/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id, HttpServletRequest request) {
        CategoriaResponseDto categoria = categoriaService.obtenerPorId(id);
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(categoria.getEmpresaId(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        categoriaService.eliminarCategoria(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Categoria eliminada correctamente", null)
        );
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Categoria.Estado estado,
            @RequestParam(required = false) String nombre,
            HttpServletRequest request
    ) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(empresaId, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        Page<CategoriaResponseDto> categoriasPage =
                categoriaService.listarCategoriasPorEmpresa(
                        empresaId,
                        page,
                        size,
                        estado,
                        nombre
                );

        Map<String, Object> data = new HashMap<>();
        data.put("categorias", categoriasPage.getContent());
        data.put("totalElements", categoriasPage.getTotalElements());
        data.put("totalPages", categoriasPage.getTotalPages());
        data.put("currentPage", categoriasPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Categorias de la empresa " + empresaId + " listadas correctamente",
                        data
                )
        );
    }
}
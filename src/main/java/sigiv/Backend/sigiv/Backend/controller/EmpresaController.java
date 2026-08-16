package sigiv.Backend.sigiv.Backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.correo.CorreoEmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.correo.CorreoEmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.services.CorreoEmpresaService;
import sigiv.Backend.sigiv.Backend.services.EmpresaService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController<empresaService> {

    private final EmpresaService empresaService;
    private final CorreoEmpresaService correoEmpresaService;
    private final JwtUtil jwtUtil;

    private Long getEmpresaIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extraerClaims(token);
        Long tokenEmpresaId = claims.get("empresa_id", Long.class);
        if (tokenEmpresaId != null) return tokenEmpresaId;
        return claims.get("id", Long.class);
    }

    // Helper method to check permissions
    private ResponseEntity<ApiResponse> checkPermissions(Long resourceId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null)
            );
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extraerClaims(token);
        Long tokenEmpresaId = claims.get("empresa_id", Long.class);
        if (tokenEmpresaId == null) tokenEmpresaId = claims.get("id", Long.class);
        if (!tokenEmpresaId.equals(resourceId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(false, HttpStatus.FORBIDDEN.value(), "No tienes permiso para acceder a esta información", null)
            );
        }
        return null; // All good
    }

    @PostMapping("/crear-empresa")
    public ResponseEntity<ApiResponse<EmpresaResponseDto>> crear(@RequestBody EmpresaRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(dto.getId_Empresa(), request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;
        EmpresaResponseDto created = empresaService.crearEmpresa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "Empresa creada correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponseDto>> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        EmpresaResponseDto empresa = empresaService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Empresa encontrada", empresa)
        );
    }

    @GetMapping("/list-empresas")
    public ResponseEntity<ApiResponse<List<EmpresaResponseDto>>> listar(HttpServletRequest request) {
        Long empresaId = getEmpresaIdFromToken(request);
        if (empresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null));
        }
        List<EmpresaResponseDto> empresas = List.of(empresaService.obtenerPorId(empresaId));
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Todas las empresas listadas", empresas)
        );
    }

    @GetMapping("/list-empresas-status")
    public ResponseEntity<ApiResponse<List<EmpresaResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Empresa.Estado estado,
            HttpServletRequest request) {
        Long empresaId = getEmpresaIdFromToken(request);
        if (empresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null));
        }
        List<EmpresaResponseDto> empresas = List.of(empresaService.obtenerPorId(empresaId));
        String message = estado != null ? "Empresas listadas por estado: " + estado : "Todas las empresas listadas";
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), message, empresas));
    }

    @PutMapping("/update-empresa/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponseDto>> actualizar(
            @PathVariable Long id, @RequestBody EmpresaRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        EmpresaResponseDto actualizado = empresaService.actualizarEmpresa(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Empresa actualizada correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-empresa/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        empresaService.eliminarEmpresa(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Empresa eliminada correctamente", null)
        );
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponseDto>> cambiarEstado(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        EmpresaResponseDto actualizado = empresaService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Estado de la empresa actualizado automáticamente", actualizado)
        );
    }

    @GetMapping("/{id}/categorias")
    public ResponseEntity<ApiResponse<Object>> listarCategoriasPorEmpresa(
            @PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        List<CategoriaResponseDto> categorias = empresaService.categoriasEmpresa(id);
        int totalElements = categorias.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<CategoriaResponseDto> paginated = categorias.subList(Math.min(fromIndex, totalElements), toIndex);
        var data = new java.util.HashMap<String, Object>();
        data.put("totalPages", (int) Math.ceil((double) totalElements / size));
        data.put("currentPage", page);
        data.put("totalElements", totalElements);
        data.put("categorias", paginated);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Categorías de la empresa con id " + id, data));
    }

    @GetMapping("/{id}/proveedores")
    public ResponseEntity<ApiResponse<Object>> listarProveedoresPorEmpresa(
            @PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        List<ProveedorResponseDto> proveedores = empresaService.proveedoresEmpresa(id);
        int totalElements = proveedores.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<ProveedorResponseDto> paginated = proveedores.subList(Math.min(fromIndex, totalElements), toIndex);
        var data = new java.util.HashMap<String, Object>();
        data.put("totalPages", (int) Math.ceil((double) totalElements / size));
        data.put("currentPage", page);
        data.put("totalElements", totalElements);
        data.put("proveedores", paginated);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Proveedores de la empresa con id " + id, data));
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDto>>> listarUsuariosPorEmpresa(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        List<UsuarioResponseDto> usuarios = empresaService.usuariosEmpresa(id);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Usuarios de la empresa con id " + id, usuarios));
    }

    @GetMapping("/{id}/total-vendido")
    public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorEmpresa(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        BigDecimal total = empresaService.calcularTotalVendido(id);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Total vendido por la empresa con id " + id, total));
    }

    @GetMapping("/{id}/total-vendido-rango")
    public ResponseEntity<ApiResponse<BigDecimal>> totalVendidoPorEmpresaEntreFechas(
            @PathVariable Long id, @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio, @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        BigDecimal total = empresaService.calcularTotalVendidoEntreFechas(id, fechaInicio, fechaFin);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Total vendido por la empresa con id " + id + " entre " + fechaInicio + " y " + fechaFin, total));
    }

    @GetMapping("/ganancia/empresa/{idEmpresa}")
    public ResponseEntity<ApiResponse<BigDecimal>> obtenerGananciaPorEmpresa(
            @PathVariable Long idEmpresa, @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio, @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(idEmpresa, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        BigDecimal ganancia;
        if (fechaInicio != null && fechaFin != null) {
            ganancia = empresaService.calcularGananciaPorEmpresaEntreFechas(idEmpresa, fechaInicio, fechaFin);
        } else {
            ganancia = empresaService.calcularGananciaPorEmpresa(idEmpresa);
        }
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Ganancia obtenida correctamente para la empresa con id " + idEmpresa, ganancia));
    }

    @GetMapping("/{id}/usuarios-activos")
    public ResponseEntity<ApiResponse<Long>> contarUsuariosActivos(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        long totalActivos = empresaService.contarUsuariosActivos(id);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Número de usuarios activos en la empresa con id " + id, totalActivos));
    }

    @PutMapping("/{id}/correo-facturacion")
    public ResponseEntity<ApiResponse<CorreoEmpresaResponseDto>> guardarCorreoFacturacion(
            @PathVariable Long id, @RequestBody CorreoEmpresaRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        CorreoEmpresaResponseDto configuracion = correoEmpresaService.guardarConfiguracion(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Correo de facturación configurado correctamente", configuracion));
    }

    @GetMapping("/{id}/correo-facturacion")
    public ResponseEntity<ApiResponse<CorreoEmpresaResponseDto>> obtenerCorreoFacturacion(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(id, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        CorreoEmpresaResponseDto configuracion = correoEmpresaService.obtenerConfiguracion(id);
        return ResponseEntity.ok(new ApiResponse<>(true, HttpStatus.OK.value(), "Configuración de correo de facturación obtenida correctamente", configuracion));
    }
}
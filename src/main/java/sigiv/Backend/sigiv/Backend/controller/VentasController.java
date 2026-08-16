package sigiv.Backend.sigiv.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.ResumenVendedorDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.EnviarFacturaCorreoRequestDto;
import sigiv.Backend.sigiv.Backend.services.FacturaEmailService;
import sigiv.Backend.sigiv.Backend.services.VentasService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")
public class VentasController {

    @Autowired
    private VentasService ventasService;

    @Autowired
    private FacturaEmailService facturaEmailService;

    @Autowired
    private JwtUtil jwtUtil;

    private Long getEmpresaIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        Claims claims = jwtUtil.extraerClaims(authHeader.substring(7));
        Long empresaId = claims.get("empresa_id", Long.class);
        if (empresaId != null) return empresaId;
        return claims.get("id", Long.class);
    }

    private ResponseEntity<ApiResponse<?>> checkPermissions(Long requiredEmpresaId, HttpServletRequest request) {
        Long tokenEmpresaId = getEmpresaIdFromToken(request);
        if (tokenEmpresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null));
        }
        if (!tokenEmpresaId.equals(requiredEmpresaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(false, HttpStatus.FORBIDDEN.value(), "No tienes permiso para acceder a este recurso", null));
        }
        return null;
    }

    @PostMapping("/crear-venta")
    public ResponseEntity<VentasResponseDto> crearVenta(@RequestBody VentasRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(dto.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).build();
        return ResponseEntity.ok(ventasService.crearVenta(dto));
    }

    @GetMapping("/listar-ventas")
    public ResponseEntity<List<VentasResponseDto>> listarVentas(HttpServletRequest request) {
        Long empresaId = getEmpresaIdFromToken(request);
        if (empresaId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(ventasService.listarVentasPorEmpresaPaginado(empresaId, 0, Integer.MAX_VALUE, null, null, null).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentasResponseDto> obtenerVenta(@PathVariable Long id, HttpServletRequest request) {
        VentasResponseDto venta = ventasService.obtenerVenta(id);
        Long empresaId = getEmpresaIdFromToken(request);
        if (empresaId == null || !empresaId.equals(venta.getEmpresaId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(venta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentasResponseDto> editarVenta(
            @PathVariable Long id,
            @RequestBody VentasRequestDto dto,
            HttpServletRequest request
    ) {
        VentasResponseDto existente = ventasService.obtenerVenta(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(existente.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).build();
        return ResponseEntity.ok(ventasService.editarVenta(id, dto));
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Long id, HttpServletRequest request) {
        VentasResponseDto existente = ventasService.obtenerVenta(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(existente.getEmpresaId(), request);
        if (errorResponse != null) return;
        ventasService.eliminarVenta(id);
    }

    @GetMapping("/empresa/{empresaId}/ventas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarVentasPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(required = false) Long idVenta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String cliente,
            HttpServletRequest request
    ) {
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(empresaId, request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<Map<String, Object>>) errorResponse.getBody());

        Page<VentasResponseDto> ventasPage;

        if (idVenta != null) {
            ventasPage = ventasService.buscarVentaPorIdYEmpresa(
                    empresaId,
                    idVenta,
                    page,
                    size
            );
        } else {
            ventasPage = ventasService.listarVentasPorEmpresaPaginado(
                    empresaId,
                    page,
                    size,
                    fechaInicio,
                    fechaFin,
                    cliente
            );
        }

    Map<String, Object> data = new HashMap<>();
    data.put("ventas", ventasPage.getContent());
    data.put("totalElements", ventasPage.getTotalElements());
    data.put("totalPages", ventasPage.getTotalPages());
    data.put("currentPage", ventasPage.getNumber());

    return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Ventas obtenidas correctamente",
                    data
            )
    );
}


    @GetMapping("/{id}/factura")
public ResponseEntity<byte[]> descargarFactura(@PathVariable Long id, HttpServletRequest request) {
    VentasResponseDto venta = ventasService.obtenerVenta(id);
    ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(venta.getEmpresaId(), request);
    if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).build();

    byte[] pdf = ventasService.generarFacturaPdf(id);

    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=factura-" + id + ".pdf")
            .header("Content-Type", "application/pdf")
            .body(pdf);
}

    @GetMapping("/{id}/factura-pos")
    public ResponseEntity<byte[]> descargarFacturaPos(@PathVariable Long id, HttpServletRequest request) {
        VentasResponseDto venta = ventasService.obtenerVenta(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(venta.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).build();
        byte[] pdf = ventasService.generarFacturaPosPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=factura-pos-" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    @PostMapping("/{id}/factura/enviar-correo")
    public ResponseEntity<ApiResponse<Void>> enviarFacturaPorCorreo(
            @PathVariable Long id,
            @RequestBody EnviarFacturaCorreoRequestDto dto,
            HttpServletRequest request
    ) {
        VentasResponseDto venta = ventasService.obtenerVenta(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(venta.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body(new ApiResponse<>(false, errorResponse.getStatusCode().value(), "No tienes permiso para este recurso", null));
        facturaEmailService.enviarFacturaPorCorreo(id, dto.getCorreoDestino(), dto.getFormatoFactura());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Factura enviada correctamente por correo",
                        null
                )
        );
    }

    @GetMapping("/empresa/{empresaId}/resumen-vendedores")
    public ResponseEntity<ApiResponse<List<ResumenVendedorDto>>> resumenVendedores(
            @PathVariable Long empresaId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            HttpServletRequest request
    ) {
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(empresaId, request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<List<ResumenVendedorDto>>) errorResponse.getBody());
        List<ResumenVendedorDto> resumen = ventasService.resumenVentasPorUsuario(empresaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Resumen de vendedores obtenido correctamente", resumen)
        );
    }

    @GetMapping("/usuario/{usuarioId}/ventas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarVentasPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Long idVenta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String cliente,
            HttpServletRequest request
    ) {
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(request.getParameter("empresaId") != null ? Long.valueOf(request.getParameter("empresaId")) : getEmpresaIdFromToken(request), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<Map<String, Object>>) errorResponse.getBody());

        Page<VentasResponseDto> ventasPage;

        if (idVenta != null) {
            ventasPage = ventasService.buscarVentaPorIdYUsuario(
                    usuarioId,
                    idVenta,
                    page,
                    size
            );
        } else {
            ventasPage = ventasService.listarVentasPorUsuarioPaginado(
                    usuarioId,
                    page,
                    size,
                    fechaInicio,
                    fechaFin,
                    cliente
            );
        }

        Map<String, Object> data = new HashMap<>();
        data.put("ventas", ventasPage.getContent());
        data.put("totalElements", ventasPage.getTotalElements());
        data.put("totalPages", ventasPage.getTotalPages());
        data.put("currentPage", ventasPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Ventas obtenidas correctamente",
                        data
                )
        );
    }

    @GetMapping("/test-smtp")
    public ResponseEntity<String> probarSmtp() {

        facturaEmailService.probarConexionSmtp();

        return ResponseEntity.ok("Conexión SMTP correcta");
    }
}
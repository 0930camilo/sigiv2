package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.abono.AbonoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.abono.AbonoResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.services.VentasService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@RestController
@RequiredArgsConstructor
public class AbonoController {

    private final VentasService ventasService;
    private final JwtUtil jwtUtil;

    private Long getEmpresaIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.extraerClaims(token);
        return claims.get("id", Long.class);
    }

    private ResponseEntity<ApiResponse> checkPermissions(Long ventaId, HttpServletRequest request) {
        Long tokenEmpresaId = getEmpresaIdFromToken(request);
        if (tokenEmpresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null)
            );
        }
        VentasResponseDto venta = ventasService.obtenerVenta(ventaId);
        if (venta.getEmpresaId() == null || !venta.getEmpresaId().equals(tokenEmpresaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(false, HttpStatus.FORBIDDEN.value(), "No tienes permiso para acceder a este recurso", null)
            );
        }
        return null; // All good
    }

    @PostMapping("/ventas/{ventaId}/abonos")
    public ResponseEntity<ApiResponse<AbonoResponseDto>> registrarAbono(
            @PathVariable Long ventaId,
            @RequestBody AbonoRequestDto abonoDto,
            HttpServletRequest request) {
        
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(ventaId, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        AbonoResponseDto nuevoAbono = ventasService.registrarAbono(ventaId, abonoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "Abono registrado correctamente", nuevoAbono)
        );
    }

    @GetMapping("/ventas/{ventaId}/abonos")
    public ResponseEntity<ApiResponse<List<AbonoResponseDto>>> getAbonosByVentaId(
            @PathVariable Long ventaId,
            HttpServletRequest request) {
        
        ResponseEntity<ApiResponse> errorResponse = checkPermissions(ventaId, request);
        if (errorResponse != null) return (ResponseEntity) errorResponse;

        List<AbonoResponseDto> abonos = ventasService.getAbonosByVentaId(ventaId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "Abonos obtenidos correctamente", abonos)
        );
    }
}
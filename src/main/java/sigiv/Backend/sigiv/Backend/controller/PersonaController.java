package sigiv.Backend.sigiv.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaResponseDto;

import sigiv.Backend.sigiv.Backend.entity.Persona;
import sigiv.Backend.sigiv.Backend.services.PersonaService;
import sigiv.Backend.sigiv.Backend.services.EmpresaService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;
    private final JwtUtil jwtUtil;

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

    @PostMapping("/crear-persona")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> crear(@RequestBody PersonaRequestDto dto, HttpServletRequest request) {
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(dto.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<PersonaResponseDto>) errorResponse.getBody());
        PersonaResponseDto created = personaService.crearPersona(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Persona creada correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        PersonaResponseDto persona = personaService.obtenerPorId(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(persona.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<PersonaResponseDto>) errorResponse.getBody());
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Persona encontrada", persona)
        );
    }

    @GetMapping("/list-personas")
    public ResponseEntity<ApiResponse<List<PersonaResponseDto>>> listar(HttpServletRequest request) {
        Long empresaId = getEmpresaIdFromToken(request);
        if (empresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null));
        }
        List<PersonaResponseDto> personas = personaService.listarPorEmpresa(empresaId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todas las personas listadas", personas)
        );
    }

    @GetMapping("/list-persona-status")
    public ResponseEntity<ApiResponse<List<PersonaResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Persona.Estado estado,
            HttpServletRequest request) {

        Long empresaId = getEmpresaIdFromToken(request);
        if (empresaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, HttpStatus.UNAUTHORIZED.value(), "Token no proporcionado o inválido", null));
        }

        List<PersonaResponseDto> personas;
        String message;

        if (estado != null) {
            personas = personaService.filtrarPorEmpresa(empresaId, estado, null, null, 0, Integer.MAX_VALUE).getContent();
            message = "Personas listadas por estado: " + estado;
        } else {
            personas = personaService.listarPorEmpresa(empresaId);
            message = "Todas las personas listadas";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, personas)
        );
    }

    @PutMapping("/update-persona/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody PersonaRequestDto dto,
            HttpServletRequest request) {
        PersonaResponseDto existente = personaService.obtenerPorId(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(existente.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<PersonaResponseDto>) errorResponse.getBody());
        PersonaResponseDto actualizado = personaService.actualizarPersona(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Persona actualizada correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-persona/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id, HttpServletRequest request) {
        PersonaResponseDto existente = personaService.obtenerPorId(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(existente.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<Void>) errorResponse.getBody());
        personaService.eliminarPersona(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Persona eliminada correctamente", null)
        );
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Persona.Estado estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "false") boolean exacto,
            HttpServletRequest request) {

        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(empresaId, request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<Map<String, Object>>) errorResponse.getBody());

        Page<PersonaResponseDto> personasPage;
        if (estado != null || documento != null || nombre != null) {
            if (exacto && documento != null) {
                personasPage = personaService.filtrarPorEmpresaExacto(empresaId, estado, documento, nombre, page, size);
            } else {
                personasPage = personaService.filtrarPorEmpresa(empresaId, estado, documento, nombre, page, size);
            }
        } else {
            personasPage = personaService.listarPorEmpresaPaginado(empresaId, page, size);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("personas", personasPage.getContent());
        data.put("totalElements", personasPage.getTotalElements());
        data.put("totalPages", personasPage.getTotalPages());
        data.put("currentPage", personasPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Personas de la empresa listadas correctamente", data)
        );
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> cambiarEstado(@PathVariable Long id, HttpServletRequest request) {
        PersonaResponseDto existente = personaService.obtenerPorId(id);
        ResponseEntity<ApiResponse<?>> errorResponse = checkPermissions(existente.getEmpresaId(), request);
        if (errorResponse != null) return ResponseEntity.status(errorResponse.getStatusCode()).body((ApiResponse<PersonaResponseDto>) errorResponse.getBody());
        PersonaResponseDto actualizado = personaService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado de la persona actualizado automáticamente", actualizado)
        );
}


}

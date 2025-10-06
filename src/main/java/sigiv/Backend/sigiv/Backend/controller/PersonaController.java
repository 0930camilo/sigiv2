package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaResponseDto;

import sigiv.Backend.sigiv.Backend.entity.Persona;
import sigiv.Backend.sigiv.Backend.services.PersonaService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @PostMapping("/crear-persona")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> crear(@RequestBody PersonaRequestDto dto) {
        PersonaResponseDto created = personaService.crearPersona(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Persona creada correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> obtenerPorId(@PathVariable Long id) {
        PersonaResponseDto persona = personaService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Persona encontrada", persona)
        );
    }

    @GetMapping("/list-personas")
    public ResponseEntity<ApiResponse<List<PersonaResponseDto>>> listar() {
        List<PersonaResponseDto> personas = personaService.listarPersonas();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todas las personas listadas", personas)
        );
    }

    @GetMapping("/list-persona-status")
    public ResponseEntity<ApiResponse<List<PersonaResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Persona.Estado estado) {

        List<PersonaResponseDto> personas;
        String message;

        if (estado != null) {
            personas = personaService.listarPorEstado(estado);
            message = "Personas listadas por estado: " + estado;
        } else {
            personas = personaService.listarPersonas();
            message = "Todas las personas listadas";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, personas)
        );
    }

    @PutMapping("/update-persona/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody PersonaRequestDto dto) {
        PersonaResponseDto actualizado = personaService.actualizarPersona(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Persona actualizada correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-persona/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        personaService.eliminarPersona(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Persona eliminada correctamente", null)
        );
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<PersonaResponseDto>> cambiarEstado(@PathVariable Long id) {
        PersonaResponseDto actualizado = personaService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado de la persona actualizado automáticamente", actualizado)
        );
}


}

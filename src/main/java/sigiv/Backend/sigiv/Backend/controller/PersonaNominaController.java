package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaResponseDto;
import sigiv.Backend.sigiv.Backend.services.PersonaNominaService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/persona-nomina")
@RequiredArgsConstructor
public class PersonaNominaController {

    private final PersonaNominaService personaNominaService;

    // ✅ Crear relación Persona-Nómina
    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<PersonaNominaResponseDto>> crear(@RequestBody PersonaNominaRequestDto dto) {
        PersonaNominaResponseDto creada = personaNominaService.crearPersonaNomina(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Registro Persona-Nómina creado correctamente", creada)
        );
    }

    // ✅ Obtener relación por ID de persona
    @GetMapping("/{idPersona}")
    public ResponseEntity<ApiResponse<PersonaNominaResponseDto>> obtenerPorId(@PathVariable Long idPersona) {
        PersonaNominaResponseDto personaNomina = personaNominaService.obtenerPorId(idPersona);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Registro Persona-Nómina encontrado", personaNomina)
        );
    }

    // ✅ Listar todas las relaciones Persona-Nómina
    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<List<PersonaNominaResponseDto>>> listar() {
        List<PersonaNominaResponseDto> lista = personaNominaService.listarPersonas();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todas las relaciones Persona-Nómina listadas correctamente", lista)
        );
    }

    // ✅ Actualizar relación Persona-Nómina
    @PutMapping("/actualizar/{idPersona}")
    public ResponseEntity<ApiResponse<PersonaNominaResponseDto>> actualizar(
            @PathVariable Long idPersona,
            @RequestBody PersonaNominaRequestDto dto) {
        PersonaNominaResponseDto actualizada = personaNominaService.actualizarPersonaNomina(idPersona, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Registro Persona-Nómina actualizado correctamente", actualizada)
        );
    }

    // ✅ Eliminar relación Persona-Nómina
    @DeleteMapping("/eliminar/{idPersona}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long idPersona) {
        personaNominaService.eliminarPersona(idPersona);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Registro Persona-Nómina eliminado correctamente", null)
        );
    }

    // ⚠️ No aplica cambiarEstado, ya que PersonaNomina no tiene atributo 'estado'
    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<String>> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ApiResponse<>(false, HttpStatus.NOT_IMPLEMENTED.value(),
                        "Este endpoint no aplica: PersonaNomina no tiene campo 'estado'", null)
        );
    }
}

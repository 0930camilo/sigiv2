package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Nomina;
import sigiv.Backend.sigiv.Backend.services.NominaService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/nominas")
@RequiredArgsConstructor
public class NominaController {

    private final NominaService nominaService;

    // ✅ Crear nómina
    @PostMapping("/crear-nomina")
    public ResponseEntity<ApiResponse<NominaResponseDto>> crear(@RequestBody NominaRequestDto dto) {
        NominaResponseDto created = nominaService.crearNomina(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Nómina creada correctamente", created)
        );
    }

    // ✅ Obtener nómina por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NominaResponseDto>> obtenerPorId(@PathVariable Long id) {
        NominaResponseDto nomina = nominaService.obtenerNominaPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Nómina encontrada", nomina)
        );
    }

    // ✅ Listar todas las nóminas
    @GetMapping("/listar")
    public ResponseEntity<ApiResponse<List<NominaResponseDto>>> listar() {
        List<NominaResponseDto> nominas = nominaService.listarNominas();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todas las nóminas listadas correctamente", nominas)
        );
    }

    // ✅ Listar nóminas por estado
    @GetMapping("/listar-por-estado")
    public ResponseEntity<ApiResponse<List<NominaResponseDto>>> listarPorEstado(
            @RequestParam Nomina.Estado estado) {

        List<NominaResponseDto> nominas = nominaService.listarPorEstado(estado);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Nóminas listadas con estado: " + estado, nominas)
        );
    }

    // ✅ Actualizar nómina
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ApiResponse<NominaResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody NominaRequestDto dto) {
        NominaResponseDto actualizado = nominaService.actualizarNomina(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Nómina actualizada correctamente", actualizado)
        );
    }

    // ✅ Eliminar nómina
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        nominaService.eliminarNomina(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Nómina eliminada correctamente", null)
        );
    }

    // ✅ Cambiar estado automáticamente
    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<NominaResponseDto>> cambiarEstado(@PathVariable Long id) {
        NominaResponseDto actualizado = nominaService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado de la nómina actualizado correctamente", actualizado)
        );
    }
}

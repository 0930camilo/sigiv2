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

    @PostMapping("/crear-nomina")
    public ResponseEntity<ApiResponse<NominaResponseDto>> crear(@RequestBody NominaRequestDto dto) {
        NominaResponseDto created = nominaService.crearNomina(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(),
                        "Nomina creada correctamente", created)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NominaResponseDto>> obtenerPorId(@PathVariable Long id) {
        NominaResponseDto nomina = nominaService.obtenerPorId(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Nomina encontrada", nomina)
        );
    }

    @GetMapping("/list-nominas")
    public ResponseEntity<ApiResponse<List<NominaResponseDto>>> listar() {
        List<NominaResponseDto> nominas = nominaService.listarNominas();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Todas las nominas listadas", nominas)
        );
    }

    @GetMapping("/list-nomina-status")
    public ResponseEntity<ApiResponse<List<NominaResponseDto>>> listarPorEstado(
            @RequestParam(required = false) Nomina.Estado estado) {

        List<NominaResponseDto> nominas;
        String message;

        if (estado != null) {
            nominas = nominaService.listarPorEstado(estado);
            message = "Nominas listadas por estado: " + estado;
        } else {
            nominas = nominaService.listarNominas();
            message = "Todas las nominas listadas";
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), message, nominas)
        );
    }

    @PutMapping("/update-nomina/{id}")
    public ResponseEntity<ApiResponse<NominaResponseDto>> actualizar(
            @PathVariable Long id,
            @RequestBody NominaRequestDto dto) {
        NominaResponseDto actualizado = nominaService.actualizarNomina(id, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Nomina actualizada correctamente", actualizado)
        );
    }

    @DeleteMapping("/delete-nomina/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        nominaService.eliminarNomina(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Nomina eliminada correctamente", null)
        );
    }

    @PutMapping("/cambiar-estado/{id}")
    public ResponseEntity<ApiResponse<NominaResponseDto>> cambiarEstado(@PathVariable Long id) {
        NominaResponseDto actualizado = nominaService.cambiarEstado(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Estado de la nomina actualizado automáticamente", actualizado)
        );
}


}

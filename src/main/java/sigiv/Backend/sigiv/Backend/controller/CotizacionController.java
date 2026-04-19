package sigiv.Backend.sigiv.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.services.CotizacionService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/cotizaciones")
@CrossOrigin(origins = "*")
public class CotizacionController {

    @Autowired
    private CotizacionService cotizacionService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearCotizacion(@RequestBody CotizacionRequestDto dto) {
        try {
            CotizacionResponseDto response = cotizacionService.crearCotizacion(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of(
                    "error", e.getMessage(),
                    "causa", e.getCause() != null ? e.getCause().toString() : "Sin causa interna"
                )
            );
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<CotizacionResponseDto>> listarCotizaciones() {
        return ResponseEntity.ok(cotizacionService.listarCotizaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponseDto> obtenerCotizacion(@PathVariable Long id) {
        return ResponseEntity.ok(cotizacionService.obtenerCotizacion(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCotizacion(@PathVariable Long id) {
        cotizacionService.eliminarCotizacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String nombreCliente,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        Page<CotizacionResponseDto> cotizacionesPage =
                cotizacionService.listarCotizacionesPorEmpresa(
                        empresaId,
                        page,
                        size,
                        usuarioId,
                        nombreCliente,
                        fechaInicio,
                        fechaFin
                );

        Map<String, Object> data = new HashMap<>();
        data.put("cotizaciones", cotizacionesPage.getContent());
        data.put("totalElements", cotizacionesPage.getTotalElements());
        data.put("totalPages", cotizacionesPage.getTotalPages());
        data.put("currentPage", cotizacionesPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Cotizaciones de la empresa " + empresaId + " listadas correctamente",
                        data
                )
        );
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarCotizacion(@PathVariable Long id) {
        byte[] pdf = cotizacionService.generarCotizacionPdf(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=cotizacion-" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }
}

package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.services.CotizacionService;

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
        e.printStackTrace(); // 👉 mostrará el error en la consola de Spring

        return ResponseEntity.status(500).body(
            java.util.Map.of(
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
}

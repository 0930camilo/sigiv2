package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionResponseDto;
import sigiv.Backend.sigiv.Backend.services.DevolucionService;

@RestController
@RequestMapping("/api/devoluciones")
@CrossOrigin(origins = "*")
public class DevolucionController {

    @Autowired
    private DevolucionService devolucionService;

    // 🟢 Registrar devolución
    @PostMapping("/registrar")
    public ResponseEntity<DevolucionResponseDto> registrar(@RequestBody DevolucionRequestDto dto) {
        DevolucionResponseDto respuesta = devolucionService.registrarDevolucion(dto);
        return ResponseEntity.ok(respuesta);
    }

    // 📋 Listar devoluciones de una venta
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<DevolucionResponseDto>> listarPorVenta(@PathVariable Long ventaId) {
        List<DevolucionResponseDto> devoluciones = devolucionService.listarDevolucionesPorVenta(ventaId);
        return ResponseEntity.ok(devoluciones);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<DevolucionResponseDto>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(required = false) Long ventaId) {
        List<DevolucionResponseDto> devoluciones = devolucionService.listarDevolucionesPorEmpresa(empresaId, ventaId);
        return ResponseEntity.ok(devoluciones);
    }
}

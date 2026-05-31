package sigiv.Backend.sigiv.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionResponseDto;
import sigiv.Backend.sigiv.Backend.services.DevolucionService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

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
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(required = false) Long ventaId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (ventaId != null) {
            List<DevolucionResponseDto> devoluciones = devolucionService.listarDevolucionesPorEmpresa(empresaId, ventaId, usuarioId);
            Map<String, Object> data = new HashMap<>();
            data.put("devoluciones", devoluciones);
            data.put("totalElements", devoluciones.size());
            data.put("totalPages", 1);
            data.put("currentPage", 0);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, HttpStatus.OK.value(),
                            "Devoluciones obtenidas correctamente", data)
            );
        }

        Page<DevolucionResponseDto> devolucionesPage = devolucionService.listarDevolucionesPorEmpresaPaginado(empresaId, page, size, usuarioId);

        Map<String, Object> data = new HashMap<>();
        data.put("devoluciones", devolucionesPage.getContent());
        data.put("totalElements", devolucionesPage.getTotalElements());
        data.put("totalPages", devolucionesPage.getTotalPages());
        data.put("currentPage", devolucionesPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Devoluciones obtenidas correctamente", data)
        );
    }
}

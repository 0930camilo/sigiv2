package sigiv.Backend.sigiv.Backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.services.VentasService;
import sigiv.Backend.sigiv.Backend.util.ApiResponse;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")
public class VentasController {

    @Autowired
    private VentasService ventasService;

    @PostMapping("/crear-venta")
    public VentasResponseDto crearVenta(@RequestBody VentasRequestDto dto) {
        return ventasService.crearVenta(dto);
    }

    @GetMapping("/listar-ventas")
    public List<VentasResponseDto> listarVentas() {
        return ventasService.listarVentas();
    }

    @GetMapping("/{id}")
    public VentasResponseDto obtenerVenta(@PathVariable Long id) {
        return ventasService.obtenerVenta(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentasResponseDto> editarVenta(
            @PathVariable Long id,
            @RequestBody VentasRequestDto dto
    ) {
        return ResponseEntity.ok(ventasService.editarVenta(id, dto));
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Long id) {
        ventasService.eliminarVenta(id);
    }

    // ✅ ENDPOINT CORRECTO
    @GetMapping("/empresa/{empresaId}/ventas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarVentasPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<VentasResponseDto> ventasPage =
                ventasService.listarVentasPorEmpresaPaginado(empresaId, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("ventas", ventasPage.getContent());
        data.put("totalElements", ventasPage.getTotalElements());
        data.put("totalPages", ventasPage.getTotalPages());
        data.put("currentPage", ventasPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Ventas de la empresa " + empresaId + " listadas correctamente",
                        data
                )
        );
    }
}

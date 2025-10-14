package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.services.VentasService;

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

    // ✏️ Editar venta
    @PutMapping("/{id}")
    public ResponseEntity<VentasResponseDto> editarVenta(@PathVariable Long id, @RequestBody VentasRequestDto dto) {
        VentasResponseDto updatedVenta = ventasService.editarVenta(id, dto);
        return ResponseEntity.ok(updatedVenta);
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Long id) {
        ventasService.eliminarVenta(id);
    }
}

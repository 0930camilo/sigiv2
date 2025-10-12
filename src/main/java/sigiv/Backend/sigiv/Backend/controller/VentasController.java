package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/crear")
    public VentasResponseDto crearVenta(@RequestBody VentasRequestDto dto) {
        return ventasService.crearVenta(dto);
    }

    @GetMapping("/listar")
    public List<VentasResponseDto> listarVentas() {
        return ventasService.listarVentas();
    }

    @GetMapping("/{id}")
    public VentasResponseDto obtenerVenta(@PathVariable Long id) {
        return ventasService.obtenerVenta(id);
    }

    @DeleteMapping("/{id}")
    public void eliminarVenta(@PathVariable Long id) {
        ventasService.eliminarVenta(id);
    }
}

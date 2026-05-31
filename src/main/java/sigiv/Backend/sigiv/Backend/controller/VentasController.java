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
import sigiv.Backend.sigiv.Backend.dto.ventas.ResumenVendedorDto;
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

 @GetMapping("/empresa/{empresaId}/ventas")
public ResponseEntity<ApiResponse<Map<String, Object>>> listarVentasPorEmpresa(
        @PathVariable Long empresaId,
        @RequestParam(required = false) Long idVenta,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
) {

    Page<VentasResponseDto> ventasPage;

    if (idVenta != null) {
        ventasPage = ventasService.buscarVentaPorIdYEmpresa(
                empresaId,
                idVenta,
                page,
                size
        );
    } else {
        ventasPage = ventasService.listarVentasPorEmpresaPaginado(
                empresaId,
                page,
                size
        );
    }

    Map<String, Object> data = new HashMap<>();
    data.put("ventas", ventasPage.getContent());
    data.put("totalElements", ventasPage.getTotalElements());
    data.put("totalPages", ventasPage.getTotalPages());
    data.put("currentPage", ventasPage.getNumber());

    return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    HttpStatus.OK.value(),
                    "Ventas obtenidas correctamente",
                    data
            )
    );
}


    @GetMapping("/{id}/factura")
public ResponseEntity<byte[]> descargarFactura(@PathVariable Long id) {

    byte[] pdf = ventasService.generarFacturaPdf(id);

    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=factura-" + id + ".pdf")
            .header("Content-Type", "application/pdf")
            .body(pdf);
}

    @GetMapping("/empresa/{empresaId}/resumen-vendedores")
    public ResponseEntity<ApiResponse<List<ResumenVendedorDto>>> resumenVendedores(
            @PathVariable Long empresaId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin
    ) {
        List<ResumenVendedorDto> resumen = ventasService.resumenVentasPorUsuario(empresaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(),
                        "Resumen de vendedores obtenido correctamente", resumen)
        );
    }

    @GetMapping("/usuario/{usuarioId}/ventas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listarVentasPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Long idVenta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<VentasResponseDto> ventasPage;

        if (idVenta != null) {
            ventasPage = ventasService.buscarVentaPorIdYUsuario(
                    usuarioId,
                    idVenta,
                    page,
                    size
            );
        } else {
            ventasPage = ventasService.listarVentasPorUsuarioPaginado(
                    usuarioId,
                    page,
                    size
            );
        }

        Map<String, Object> data = new HashMap<>();
        data.put("ventas", ventasPage.getContent());
        data.put("totalElements", ventasPage.getTotalElements());
        data.put("totalPages", ventasPage.getTotalPages());
        data.put("currentPage", ventasPage.getNumber());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        HttpStatus.OK.value(),
                        "Ventas obtenidas correctamente",
                        data
                )
        );
    }


}

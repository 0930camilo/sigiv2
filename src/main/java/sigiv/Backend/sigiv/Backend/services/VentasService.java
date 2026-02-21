package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;

public interface VentasService {

    VentasResponseDto crearVenta(VentasRequestDto dto);

    List<VentasResponseDto> listarVentas();

    VentasResponseDto obtenerVenta(Long id);

    VentasResponseDto editarVenta(Long id, VentasRequestDto dto);

    void eliminarVenta(Long id);

    // ✅ Page CORRECTO (Spring)
    Page<VentasResponseDto> listarVentasPorEmpresaPaginado(
            Long empresaId,
            int page,
            int size
    );

       // ✅ AGREGA ESTO
    byte[] generarFacturaPdf(Long id);


    Page<VentasResponseDto> buscarVentaPorIdYEmpresa(
        Long empresaId,
        Long idVenta,
        int page,
        int size
);

}

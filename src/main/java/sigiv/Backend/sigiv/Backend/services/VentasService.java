package sigiv.Backend.sigiv.Backend.services;


import java.util.List;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;

public interface VentasService {
    VentasResponseDto crearVenta(VentasRequestDto dto);
    List<VentasResponseDto> listarVentas();
    VentasResponseDto obtenerVenta(Long id);
    void eliminarVenta(Long id);
}

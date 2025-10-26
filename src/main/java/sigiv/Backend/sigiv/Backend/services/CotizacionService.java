package sigiv.Backend.sigiv.Backend.services;

import java.util.List;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;

public interface CotizacionService {
    CotizacionResponseDto crearCotizacion(CotizacionRequestDto dto);
    List<CotizacionResponseDto> listarCotizaciones();
    CotizacionResponseDto obtenerCotizacion(Long id);
    void eliminarCotizacion(Long id);
    List<DetalleCotizacion> findByCotizacionId(Long idcotizacion);

}


package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;

public interface DetalleCotizacionService {
    DetalleCotizacionResponseDto agregarDetalleACotizacion(Long cotizacionId, DetalleCotizacionRequestDto dto);
    void eliminarDetalle(Long detalleId);
    void eliminarTodosLosDetallesDeCotizacion(Long cotizacionId);
    List<DetalleCotizacion> findByCotizacionId(Long idcotizacion);

}

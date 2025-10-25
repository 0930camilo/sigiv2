package sigiv.Backend.sigiv.Backend.dto.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Cotizacion;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;
import sigiv.Backend.sigiv.Backend.entity.Producto;

@Component
public class DetalleCotizacionMapper {

    public DetalleCotizacion toEntity(DetalleCotizacionRequestDto dto, Cotizacion cot, Producto producto, BigDecimal subtotal) {
        DetalleCotizacion detalle = new DetalleCotizacion();
        detalle.setCotizacion(cot);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecio(producto.getPrecio());
        detalle.setSubtotal(subtotal);
        detalle.setDescripcionProducto(producto.getNombre());
        return detalle;
    }

    public DetalleCotizacionResponseDto toDto(DetalleCotizacion detalle) {
        DetalleCotizacionResponseDto dto = new DetalleCotizacionResponseDto();
        dto.setDescripcionProducto(detalle.getDescripcionProducto());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecio(detalle.getPrecio());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }
}

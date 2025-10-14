package sigiv.Backend.sigiv.Backend.dto.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleVentas;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Ventas;

@Component
public class DetalleVentaMapper {

    public DetalleVentas toEntity(DetalleVentaRequestDto dto, Ventas venta, Producto producto, BigDecimal subtotal) {
        DetalleVentas detalle = new DetalleVentas();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecio(producto.getPrecio());
        detalle.setSubtotal(subtotal);
        detalle.setDescripcionProducto(producto.getNombre());
        return detalle;
    }

    public DetalleVentaResponseDto toDto(DetalleVentas entity) {
        return new DetalleVentaResponseDto(
                entity.getDescripcionProducto(),
                entity.getCantidad(),
                entity.getPrecio(),
                entity.getSubtotal()
        );
    }
}

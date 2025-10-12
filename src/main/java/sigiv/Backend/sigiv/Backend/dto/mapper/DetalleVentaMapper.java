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

    // construye entidad a partir del request y el producto + venta
    public DetalleVentas toEntity(DetalleVentaRequestDto dto, Producto producto, Ventas venta) {
        DetalleVentas entity = new DetalleVentas();
        entity.setProducto(producto);
        entity.setVenta(venta);
        entity.setCantidad(dto.getCantidad());
        entity.setPrecio(producto.getPrecio()); // precio tomado del producto
        entity.setDescripcionProducto(
            dto.getDescripcionProducto() != null ? dto.getDescripcionProducto() : producto.getNombre()
        );

        // Calcula subtotal en Java (evita depender de columnas generated always)
        if (entity.getCantidad() != null && entity.getPrecio() != null) {
            entity.setSubtotal(entity.getPrecio().multiply(
                java.math.BigDecimal.valueOf(entity.getCantidad())));
        } else {
            entity.setSubtotal(java.math.BigDecimal.ZERO);
        }
        return entity;
    }

    public DetalleVentaResponseDto toDto(DetalleVentas entity) {
        DetalleVentaResponseDto dto = new DetalleVentaResponseDto();
        dto.setIddetalle(entity.getIddetalle());
        dto.setProductoId(entity.getProducto() != null ? entity.getProducto().getIdproducto() : null);
        dto.setDescripcionProducto(entity.getDescripcionProducto());
        dto.setCantidad(entity.getCantidad());
        dto.setPrecio(entity.getPrecio());
        dto.setSubtotal(entity.getSubtotal());
        return dto;
    }
}

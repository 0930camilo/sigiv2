package sigiv.Backend.sigiv.Backend.dto.mapper;

import org.springframework.stereotype.Component;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Devolucion;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Ventas;

@Component
public class DevolucionMapper {

    // Convertir de DTO de solicitud a entidad
    public Devolucion toEntity(DevolucionRequestDto dto, Ventas venta, Producto producto) {
        if (dto == null || venta == null || producto == null) {
            throw new IllegalArgumentException("Datos insuficientes para crear la devolución");
        }

        Devolucion devolucion = new Devolucion();
        devolucion.setVenta(venta);
        devolucion.setProducto(producto);
        devolucion.setCantidad(dto.getCantidad());
        devolucion.setMotivo(dto.getMotivo());
        return devolucion;
    }

    // Convertir de entidad a DTO de respuesta
    public DevolucionResponseDto toResponseDto(Devolucion devolucion) {
        if (devolucion == null) {
            return null;
        }

        DevolucionResponseDto dto = new DevolucionResponseDto();
        dto.setIddevolucion(devolucion.getIdDevolucion());
        dto.setVentaId(devolucion.getVenta() != null ? devolucion.getVenta().getIdventa() : null);
        dto.setProductoId(devolucion.getProducto() != null ? devolucion.getProducto().getIdproducto() : null);
        dto.setNombreProducto(devolucion.getProducto() != null ? devolucion.getProducto().getNombre() : null);
        dto.setCantidad(devolucion.getCantidad());
        dto.setMotivo(devolucion.getMotivo());
        dto.setFecha(devolucion.getFecha());
        return dto;
    }
}

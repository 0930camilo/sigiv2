package sigiv.Backend.sigiv.Backend.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.entity.Ventas;

@Component
public class VentasMapper {

    @Autowired
    private DetalleVentaMapper detalleMapper;

    public Ventas toEntity(VentasRequestDto dto, Usuario usuario) {
        Ventas venta = new Ventas();
        venta.setUsuario(usuario);
        venta.setNombreCliente(dto.getNombreCliente());
        venta.setTelefonoCliente(dto.getTelefonoCliente());
        venta.setEfectivo(dto.getEfectivo());
        return venta;
    }

    public VentasResponseDto toDto(Ventas entity) {
        List<DetalleVentaResponseDto> detalles = entity.getDetalles() != null
                ? entity.getDetalles().stream()
                        .map(detalleMapper::toDto)
                        .collect(Collectors.toList())
                : null;

        return new VentasResponseDto(
                entity.getIdventa(),
                entity.getFecha(),
                entity.getNombreCliente(),
                entity.getTelefonoCliente(),
                entity.getTotal(),
                entity.getEfectivo(),
                entity.getCambio(),
                entity.getUsuario() != null ? entity.getUsuario().getNombres() : null,
                detalles
        );
    }
}

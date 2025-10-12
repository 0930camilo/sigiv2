package sigiv.Backend.sigiv.Backend.dto.mapper;


import org.springframework.stereotype.Component;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.entity.Ventas;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;

@Component
public class VentasMapper {

    public Ventas toEntity(VentasRequestDto dto, Usuario usuario) {
        Ventas entity = new Ventas();
        entity.setFecha(dto.getFecha());
        entity.setTotal(dto.getTotal());
        entity.setNombreCliente(dto.getNombreCliente());
        entity.setTelefonoCliente(dto.getTelefonoCliente());
        entity.setEfectivo(dto.getEfectivo());
        entity.setCambio(dto.getCambio());
        entity.setUsuario(usuario);
        return entity;
    }

    public VentasResponseDto toDto(Ventas entity) {
        VentasResponseDto dto = new VentasResponseDto();
        dto.setIdventa(entity.getIdventa());
        dto.setFecha(entity.getFecha());
        dto.setTotal(entity.getTotal());
        dto.setNombreCliente(entity.getNombreCliente());
        dto.setTelefonoCliente(entity.getTelefonoCliente());
        dto.setEfectivo(entity.getEfectivo());
        dto.setCambio(entity.getCambio());
        dto.setNombreUsuario(
            (entity.getUsuario() != null) ? entity.getUsuario().getNombres() : null
        );
        return dto;
    }
}
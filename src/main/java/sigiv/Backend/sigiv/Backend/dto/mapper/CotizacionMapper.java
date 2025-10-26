package sigiv.Backend.sigiv.Backend.dto.mapper;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Cotizacion;
import sigiv.Backend.sigiv.Backend.entity.Usuario;

@Component
public class CotizacionMapper {

    @Autowired
    private DetalleCotizacionMapper detalleMapper;

    // Convierte de DTO a Entidad
    public Cotizacion toEntity(CotizacionRequestDto dto, Usuario usuario) {
        Cotizacion cot = new Cotizacion();
        cot.setUsuario(usuario);
        cot.setNombreCliente(dto.getNombreCliente());
        cot.setTelefonoCliente(dto.getTelefonoCliente());
        cot.setTotal(dto.getTotal());
        return cot;
    }

    // Convierte de Entidad a DTO
    public CotizacionResponseDto toDto(Cotizacion cot) {
        CotizacionResponseDto dto = new CotizacionResponseDto();
        dto.setIdcotizacion(cot.getIdcotizacion());
        dto.setFecha(cot.getFecha());
        dto.setNombreCliente(cot.getNombreCliente());
        dto.setTelefonoCliente(cot.getTelefonoCliente());
        dto.setTotal(cot.getTotal());

        if (cot.getUsuario() != null) {
            dto.setNombreUsuario(cot.getUsuario().getNombres());
        }

        // 🔹 Mapea los detalles si existen
        if (cot.getDetalles() != null && !cot.getDetalles().isEmpty()) {
            dto.setDetalles(
                cot.getDetalles().stream()
                    .map(detalleMapper::toDto)
                    .collect(Collectors.toList())
            );
        }

        return dto;
    }
}

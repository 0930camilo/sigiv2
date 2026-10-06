package sigiv.Backend.sigiv.Backend.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sigiv.Backend.sigiv.Backend.dto.abono.AbonoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.abono.AbonoResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Abono;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.entity.Ventas;

@Mapper(componentModel = "spring")
public interface AbonoMapper {

    @Mapping(target = "idAbono", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "venta", source = "venta")
    @Mapping(target = "usuario", source = "usuario")
    Abono toEntity(AbonoRequestDto dto, Ventas venta, Usuario usuario);

    @Mapping(target = "ventaId", source = "venta.idventa")
    @Mapping(target = "usuarioId", source = "usuario.idUsuario")
    @Mapping(target = "nombreUsuario", source = "usuario.nombres")
    AbonoResponseDto toDto(Abono abono);
}
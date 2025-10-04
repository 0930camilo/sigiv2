package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorRequestDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

public class ProveedorMapper {

    public static ProveedorResponseDto toDto(Proveedor p) {
        if (p == null) return null;
        return new ProveedorResponseDto(
            p.getIdproveedor(),
            p.getNombre(),
            p.getTelefono(),
            p.getDireccion(),
            p.getEstado(),
            p.getEmpresa() != null ? p.getEmpresa().getId_Empresa() : null
        );
    }

    // Crear nuevo proveedor
    public static Proveedor toEntityForCreate(ProveedorRequestDto dto, Empresa empresa) {
        Proveedor p = new Proveedor();
        updateEntityFromDto(dto, p, empresa);
        return p;
    }

  public static void updateEntityFromDto(ProveedorRequestDto dto, Proveedor entity, Empresa empresa) {
    if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
    if (dto.getTelefono() != null) entity.setTelefono(dto.getTelefono());
    if (dto.getDireccion() != null) entity.setDireccion(dto.getDireccion());
    if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
    if (empresa != null) entity.setEmpresa(empresa);
}

}

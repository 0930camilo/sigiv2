package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.user.UsuarioRequestDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Usuario;

public class UsuarioMapper {
    public static UsuarioResponseDto toDto(Usuario u) {
        if (u == null) return null;
        return new UsuarioResponseDto(
            u.getId_usuario(),
            u.getNombres(),
            u.getDireccion(),
            u.getTelefono(),
            u.getEstado(),
            u.getEmpresa() != null ? u.getEmpresa().getId_Empresa() : null
        );
    }

    public static Usuario toEntityForCreate(UsuarioRequestDto dto, Empresa empresa) {
        Usuario u = new Usuario();
        updateEntityFromDto(dto, u, empresa);
        return u;
    }

    public static void updateEntityFromDto(UsuarioRequestDto dto, Usuario entity, Empresa empresa) {
        if (dto.getNombres() != null) entity.setNombres(dto.getNombres());
        if (dto.getClave() != null) entity.setClave(dto.getClave());
        if (dto.getTelefono() != null) entity.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) entity.setDireccion(dto.getDireccion());
        if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
        if (empresa != null) entity.setEmpresa(empresa);
    }
}

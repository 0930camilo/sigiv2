package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

public class CategoriaMapper {

    public static CategoriaResponseDto toDto(Categoria c) {
        if (c == null) return null;
        return new CategoriaResponseDto(
            c.getIdcategoria(),
            c.getNombre(),
            c.getEstado(),
            c.getEmpresa() != null ? c.getEmpresa().getId_Empresa() : null
        );
    }

    // Crear nueva categoría
    public static Categoria toEntityForCreate(CategoriaRequestDto dto, Empresa empresa) {
        Categoria c = new Categoria();
        updateEntityFromDto(dto, c, empresa);
        return c;
    }

    // Actualizar categoría existente
    public static void updateEntityFromDto(CategoriaRequestDto dto, Categoria entity, Empresa empresa) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
        if (empresa != null) entity.setEmpresa(empresa);
    }
}

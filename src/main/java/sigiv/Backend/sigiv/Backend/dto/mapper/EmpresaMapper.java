package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

public class EmpresaMapper {

    public static EmpresaResponseDto toDto(Empresa e) {
        if (e == null) return null;
        return new EmpresaResponseDto(
            e.getId_Empresa(),
            e.getNombreEmpresa(),
            e.getClave(),
            e.getNit(),
            e.getTelefono(),
            e.getDireccion(),
              e.getEstado()
        );
    }

    public static Empresa toEntityForCreate(EmpresaRequestDto dto, Empresa empresa) {
        Empresa e = new Empresa();
        updateEntityFromDto(dto, e, empresa);
        return e;
    }

    public static void updateEntityFromDto(EmpresaRequestDto dto, Empresa entity, Empresa empresa) {
        if (dto.getNombre_empresa() != null) entity.setNombreEmpresa(dto.getNombre_empresa());
        if (dto.getClave() != null) entity.setClave(dto.getClave());
        if (dto.getNit() != null) entity.setNit(dto.getNit());
        entity.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) entity.setDireccion(dto.getDireccion());
      if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
    }
}

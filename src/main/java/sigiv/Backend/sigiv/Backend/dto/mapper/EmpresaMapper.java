package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

public class EmpresaMapper {

    public static EmpresaResponseDto toDto(Empresa e) {
        if (e == null) return null;
        return new EmpresaResponseDto(
            e.getIdEmpresa(),
            e.getNombreEmpresa(),
            e.getClave(),
            e.getNit(),
            e.getCorreo(),
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
        // La clave NO se actualiza aquí, se maneja en el servicio con encriptación
        if (dto.getNit() != null) entity.setNit(dto.getNit());
        if (dto.getCorreo() != null) entity.setCorreo(dto.getCorreo());
        entity.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) entity.setDireccion(dto.getDireccion());
      if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
    }
}

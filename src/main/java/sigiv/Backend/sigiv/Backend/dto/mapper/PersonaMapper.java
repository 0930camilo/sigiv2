package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.persona.PersonaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Persona;
import sigiv.Backend.sigiv.Backend.entity.Empresa;


public class PersonaMapper {

    public static PersonaResponseDto toDto(Persona p) {
        if (p == null) return null;
        return new PersonaResponseDto(
            p.getIdpersona(),
            p.getNombre(),
            p.getCorreo(),
            p.getTelefono(),
            p.getDireccion(),
            p.getFechaNacimiento(),
            p.getFechaIngreso(),
            p.getEstado(),
            p.getEmpresa() != null ? p.getEmpresa().getIdEmpresa() : null
        );
    }

    // Crear nueva persona
    public static Persona toEntityForCreate(PersonaRequestDto dto, Empresa empresa) {
        Persona p = new Persona();
        updateEntityFromDto(dto, p, empresa);
        return p;
    }

    // Actualizar persona existente
    public static void updateEntityFromDto(PersonaRequestDto dto, Persona entity, Empresa empresa) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getCorreo() != null) entity.setCorreo(dto.getCorreo());
        if (dto.getTelefono() != null) entity.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) entity.setDireccion(dto.getDireccion());
        if (dto.getFechaNacimiento() != null) entity.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getFechaIngreso() != null) entity.setFechaIngreso(dto.getFechaIngreso());
        if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
       if (empresa != null) entity.setEmpresa(empresa);
    }
}

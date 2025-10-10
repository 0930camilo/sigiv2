package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.PersonaNomina;
import sigiv.Backend.sigiv.Backend.entity.Persona;
import sigiv.Backend.sigiv.Backend.entity.Nomina;



public class PersonaNominaMapper {

    // Convertir entidad -> DTO
    public static PersonaNominaResponseDto toDto(PersonaNomina p) {
        if (p == null) return null;
        return new PersonaNominaResponseDto(
            p.getIdnomina(),
            p.getIdpersona(),
            p.getDiasTrabajados(),
            p.getValorDia(),
            p.getSalario()
        );
    }

    // Crear nueva entidad a partir del DTO
    public static PersonaNomina toEntityForCreate(PersonaNominaRequestDto dto, Persona persona, Nomina nomina) {
        PersonaNomina p = new PersonaNomina();  
        p.setIdnomina(dto.getIdNomina());
        p.setIdpersona(dto.getIdPersona());
        p.setDiasTrabajados(dto.getDiasTrabajados());
        p.setValorDia(dto.getValorDia());
        // El salario se calculará automáticamente con @PrePersist/@PreUpdate
        p.setPersona(persona);
        p.setNomina(nomina);
        return p;
    }

    // Actualizar una entidad existente
    public static void updateEntityFromDto(PersonaNominaRequestDto dto, PersonaNomina entity) {
        if (dto.getDiasTrabajados() != null) entity.setDiasTrabajados(dto.getDiasTrabajados());
        if (dto.getValorDia() != null) entity.setValorDia(dto.getValorDia());
        // El salario se recalculará automáticamente por @PreUpdate
    }
}
package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.nomina.NominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Nomina;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

public class NominaMapper {

    public static NominaResponseDto toDto(Nomina n) {
        if (n == null) return null;
        return new NominaResponseDto(
            n.getIdnomina(),
            n.getDescripcion(),
            n.getFechaInicio(),
            n.getFechaFin(),
            n.getTotalPago(),
            n.getEstado(),
            n.getEmpresa() != null ? n.getEmpresa().getIdEmpresa() : null
        );
    }

    // Crear nueva nómina
    public static Nomina toEntityForCreate(NominaRequestDto dto, Empresa empresa) {
        Nomina n = new Nomina();
        updateEntityFromDto(dto, n, empresa);
        return n;
    }


    // Actualizar nómina existente
    public static void updateEntityFromDto(NominaRequestDto dto, Nomina entity, Empresa empresa) {
        if (dto.getDescripcion() != null) entity.setDescripcion(dto.getDescripcion());
        if (dto.getFechaInicio() != null) entity.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) entity.setFechaFin(dto.getFechaFin());
        if (dto.getTotalPago() != null) entity.setTotalPago(dto.getTotalPago());
        if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
        if (empresa != null) entity.setEmpresa(empresa);
       
    
    }
}

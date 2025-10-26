package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.nomina.NominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Nomina;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

public class NominaMapper {

    public static Nomina toEntityForCreate(NominaRequestDto dto, Empresa empresa) {
        Nomina nomina = new Nomina();
        nomina.setDescripcion(dto.getDescripcion());
        nomina.setFechaInicio(dto.getFechaInicio());
        nomina.setFechaFin(dto.getFechaFin());
        nomina.setEstado(dto.getEstado());
        nomina.setEmpresa(empresa);
        nomina.setTotalPago(null);
        return nomina;
    }

    public static void updateEntityFromDto(NominaRequestDto dto, Nomina entity, Empresa empresa) {
        if (dto.getDescripcion() != null) entity.setDescripcion(dto.getDescripcion());
        if (dto.getFechaInicio() != null) entity.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) entity.setFechaFin(dto.getFechaFin());
        if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
        if (empresa != null) entity.setEmpresa(empresa);
    }

    public static NominaResponseDto toDto(Nomina entity) {
        String empresaNombre = (entity.getEmpresa() != null) ? entity.getEmpresa().getNombre_empresa() : null;
        NominaResponseDto dto = new NominaResponseDto();
        dto.setIdNomina(entity.getIdnomina());
        dto.setDescripcion(entity.getDescripcion());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setEstado(entity.getEstado());
        dto.setTotalPago(entity.getTotalPago());
        dto.setEmpresaNombre(empresaNombre);
        return dto;
    }
}

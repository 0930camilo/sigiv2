package sigiv.Backend.sigiv.Backend.services;

import sigiv.Backend.sigiv.Backend.dto.correo.CorreoEmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.correo.CorreoEmpresaResponseDto;

public interface CorreoEmpresaService {

    CorreoEmpresaResponseDto guardarConfiguracion(Long empresaId, CorreoEmpresaRequestDto dto);

    CorreoEmpresaResponseDto obtenerConfiguracion(Long empresaId);
}

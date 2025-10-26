package sigiv.Backend.sigiv.Backend.services;

import java.util.List;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionResponseDto;

public interface DevolucionService {
    DevolucionResponseDto registrarDevolucion(DevolucionRequestDto dto);
    List<DevolucionResponseDto> listarDevolucionesPorVenta(Long ventaId);
}

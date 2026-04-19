package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionResponseDto;

public interface DevolucionService {
    DevolucionResponseDto registrarDevolucion(DevolucionRequestDto dto);
    List<DevolucionResponseDto> listarDevolucionesPorVenta(Long ventaId);
    List<DevolucionResponseDto> listarDevolucionesPorEmpresa(Long empresaId, Long ventaId);
    Page<DevolucionResponseDto> listarDevolucionesPorEmpresaPaginado(Long empresaId, int page, int size);
}

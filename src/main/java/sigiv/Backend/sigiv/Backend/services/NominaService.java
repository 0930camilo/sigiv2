package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.nomina.NominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Nomina;

public interface NominaService {
    NominaResponseDto crearNomina(NominaRequestDto dto);
    NominaResponseDto obtenerNominaPorId(Long id);
    NominaResponseDto actualizarNomina(Long id, NominaRequestDto dto);
    List<NominaResponseDto> listarNominas();
    List<NominaResponseDto> listarPorEstado(Nomina.Estado estado);
    List<NominaResponseDto> listarPorEmpresa(Long empresaId);
    Page<NominaResponseDto> listarPorEmpresaPaginado(Long empresaId, int page, int size);
    void eliminarNomina(Long id);
    NominaResponseDto cambiarEstado(Long id);
}

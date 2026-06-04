package sigiv.Backend.sigiv.Backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Nomina;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.dto.mapper.NominaMapper;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.NominaRepository;
import sigiv.Backend.sigiv.Backend.repository.PersonaNominaRepository;
import sigiv.Backend.sigiv.Backend.services.NominaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NominaServiceImpl implements NominaService {

    private final NominaRepository nominaRepository;
    private final EmpresaRepository empresaRepository;
    private final PersonaNominaRepository personaNominaRepository;

    @Override
    public NominaResponseDto crearNomina(NominaRequestDto dto) {
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }

        Nomina nomina = NominaMapper.toEntityForCreate(dto, empresa);
        Nomina guardada = nominaRepository.save(nomina);

        // 🔹 Calcular totalPago automáticamente
        BigDecimal total = personaNominaRepository.calcularTotalPorNomina(guardada.getIdnomina());
        guardada.setTotalPago(total != null ? total : BigDecimal.ZERO);
        nominaRepository.save(guardada);

        return NominaMapper.toDto(guardada);
    }

    @Override
    public NominaResponseDto actualizarNomina(Long id, NominaRequestDto dto) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nomina", "id", id));

        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }

        NominaMapper.updateEntityFromDto(dto, nomina, empresa);
        Nomina actualizado = nominaRepository.save(nomina);

        // 🔹 Recalcular totalPago
        BigDecimal total = personaNominaRepository.calcularTotalPorNomina(actualizado.getIdnomina());
        actualizado.setTotalPago(total != null ? total : BigDecimal.ZERO);
        nominaRepository.save(actualizado);

        return NominaMapper.toDto(actualizado);
    }

    @Override
    public NominaResponseDto obtenerNominaPorId(Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nomina", "id", id));

        BigDecimal total = personaNominaRepository.calcularTotalPorNomina(nomina.getIdnomina());
        nomina.setTotalPago(total != null ? total : BigDecimal.ZERO);
        nominaRepository.save(nomina);

        return NominaMapper.toDto(nomina);
    }

    @Override
    public List<NominaResponseDto> listarNominas() {
        return nominaRepository.findAll().stream()
                .peek(nomina -> {
                    BigDecimal total = personaNominaRepository.calcularTotalPorNomina(nomina.getIdnomina());
                    nomina.setTotalPago(total != null ? total : BigDecimal.ZERO);
                    nominaRepository.save(nomina);
                })
                .map(NominaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NominaResponseDto> listarPorEstado(Nomina.Estado estado) {
        return nominaRepository.findAll().stream()
                .filter(n -> n.getEstado() == estado)
                .map(NominaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NominaResponseDto> listarPorEmpresa(Long empresaId) {
        return nominaRepository.findByEmpresaIdEmpresa(empresaId).stream()
                .peek(nomina -> {
                    BigDecimal total = personaNominaRepository.calcularTotalPorNomina(nomina.getIdnomina());
                    nomina.setTotalPago(total != null ? total : BigDecimal.ZERO);
                })
                .map(NominaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NominaResponseDto> listarPorEmpresaPaginado(Long empresaId, int page, int size) {
        Page<Nomina> nominasPage = nominaRepository.findByEmpresaIdEmpresa(empresaId, PageRequest.of(page, size, Sort.by("idnomina").descending()));
        return nominasPage.map(nomina -> {
            BigDecimal total = personaNominaRepository.calcularTotalPorNomina(nomina.getIdnomina());
            nomina.setTotalPago(total != null ? total : BigDecimal.ZERO);
            return NominaMapper.toDto(nomina);
        });
    }

    @Override
    public void eliminarNomina(Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nomina", "id", id));
        nominaRepository.delete(nomina);
    }

    @Override
    public NominaResponseDto cambiarEstado(Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nomina", "id", id));

        if (nomina.getEstado() == Nomina.Estado.Activo) {
            nomina.setEstado(Nomina.Estado.Inactivo);
        } else {
            nomina.setEstado(Nomina.Estado.Activo);
        }

        Nomina actualizada = nominaRepository.save(nomina);
        return NominaMapper.toDto(actualizada);
    }
}

package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.NominaMapper;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.nomina.NominaResponseDto;

import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Nomina;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.NominaRepository;
import sigiv.Backend.sigiv.Backend.services.NominaService;

@Service
@RequiredArgsConstructor
public class NominaServiceImpl implements NominaService {
    private final NominaRepository nominaRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    public NominaResponseDto crearNomina(NominaRequestDto dto) {
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("empresa no encontrada"));
        }
        Nomina nomina = NominaMapper.toEntityForCreate(dto, empresa);
        Nomina guardada = nominaRepository.save(nomina);
        return NominaMapper.toDto(guardada);
    }

    @Override
    public NominaResponseDto obtenerPorId(Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nomina", "id", id));
        return NominaMapper.toDto(nomina);
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
        return NominaMapper.toDto(actualizado);
    }





    @Override
    public List<NominaResponseDto> listarNominas() {
        return nominaRepository.findAll()
                .stream().map(NominaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NominaResponseDto> listarPorEstado(Nomina.Estado estado) {
        return nominaRepository.findByEstado(estado)
                .stream()
                .map(NominaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarNomina(Long id) {
        if (!nominaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nomina", "id", id);
        }
        nominaRepository.deleteById(id);
    }

     @Override
public NominaResponseDto cambiarEstado(Long id) {
    Nomina nomina = nominaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Nomina", "id", id));

    // Cambiar estado automáticamente
    if (nomina.getEstado() == Nomina.Estado.Activo) {
        nomina.setEstado(Nomina.Estado.Inactivo);
    } else {
        nomina.setEstado(Nomina.Estado.Activo);
    }

    Nomina actualizado = nominaRepository.save(nomina);
    return NominaMapper.toDto(actualizado);
}
}

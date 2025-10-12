package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.CategoriaMapper;
import sigiv.Backend.sigiv.Backend.dto.mapper.EmpresaMapper;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.empre.EmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.services.EmpresaService;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Override
    public EmpresaResponseDto crearEmpresa(EmpresaRequestDto dto) {
        // Siempre crea una nueva empresa desde el DTO
        Empresa empresa = EmpresaMapper.toEntityForCreate(dto, new Empresa());
        empresa.setEstado(Empresa.Estado.Activo); // Estado por defecto

        Empresa guardado = empresaRepository.save(empresa);
        return EmpresaMapper.toDto(guardado);
    }

    @Override
    public EmpresaResponseDto obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));
        return EmpresaMapper.toDto(empresa);
    }

    @Override
    public EmpresaResponseDto actualizarEmpresa(Long id, EmpresaRequestDto dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));

        // Actualizamos los datos desde el DTO
        EmpresaMapper.updateEntityFromDto(dto, empresa, empresa);

        Empresa actualizado = empresaRepository.save(empresa);
        return EmpresaMapper.toDto(actualizado);
    }

    @Override
    public List<EmpresaResponseDto> listarEmpresas() {
        return empresaRepository.findAll()
                .stream()
                .map(EmpresaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaResponseDto> listarPorEstado(Empresa.Estado estado) {
        return empresaRepository.findByEstado(estado)
                .stream()
                .map(EmpresaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarEmpresa(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa", "id", id);
        }
        empresaRepository.deleteById(id);
    }
       @Override
public EmpresaResponseDto cambiarEstado(Long id) {
    Empresa empresa = empresaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));

    // Cambiar estado automáticamente
    if (empresa.getEstado() == Empresa.Estado.Activo) {
        empresa.setEstado(Empresa.Estado.Inactivo);
    } else {
        empresa.setEstado(Empresa.Estado.Activo);
    }

    Empresa actualizado = empresaRepository.save(empresa);
    return EmpresaMapper.toDto(actualizado);
}

@Override
public List<CategoriaResponseDto> categoriasEmpresa(Long id) {
    Empresa empresa = empresaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));

    return empresa.getCategorias()
            .stream()
            .map(c -> CategoriaMapper.toDto(c))
            .collect(Collectors.toList());
}
}

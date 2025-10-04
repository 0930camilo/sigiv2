package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.ProveedorMapper;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorRequestDto;
import sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.ProveedorRepository;
import sigiv.Backend.sigiv.Backend.services.ProveedorService;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {
    private final ProveedorRepository proveedorRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    public ProveedorResponseDto crearProveedor(ProveedorRequestDto dto) {
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("empresa no encontrada"));
        }
        Proveedor proveedor = ProveedorMapper.toEntityForCreate(dto, empresa);
        Proveedor guardado = proveedorRepository.save(proveedor);
        return ProveedorMapper.toDto(guardado);
    }

    @Override
    public ProveedorResponseDto obtenerPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));
        return ProveedorMapper.toDto(proveedor);
    }

    @Override
    public ProveedorResponseDto actualizarProveedor(Long id, ProveedorRequestDto dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }

       ProveedorMapper.updateEntityFromDto(dto, proveedor, empresa);
        Proveedor actualizado = proveedorRepository.save(proveedor);
        return ProveedorMapper.toDto(actualizado);
    }

    @Override
    public List<ProveedorResponseDto> listarProveedores() {
        return proveedorRepository.findAll()
                .stream().map(ProveedorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorResponseDto> listarPorEstado(Proveedor.Estado estado) {
        return proveedorRepository.findByEstado(estado)
                .stream()
                .map(ProveedorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarProveedor(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor", "id", id);
        }
        proveedorRepository.deleteById(id);
    }

     @Override
public ProveedorResponseDto cambiarEstado(Long id) {
    Proveedor proveedor = proveedorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

    // Cambiar estado automáticamente
    if (proveedor.getEstado() == Proveedor.Estado.Activo) {
        proveedor.setEstado(Proveedor.Estado.Inactivo);
    } else {
        proveedor.setEstado(Proveedor.Estado.Activo);
    }

    Proveedor actualizado = proveedorRepository.save(proveedor);
    return ProveedorMapper.toDto(actualizado);
}

}

package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public void eliminarProveedor(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor", "id", id);
        }
        proveedorRepository.deleteById(id);
    }




@Override
public Page<ProveedorResponseDto> listarProveedoresPorEmpresa(
        Long empresaId,
        int page,
        int size,
        Proveedor.Estado estado,
        String nombre
) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("idproveedor").ascending());

    Page<Proveedor> proveedoresPage;

    boolean tieneNombre = nombre != null && !nombre.trim().isEmpty();

    if (estado != null && tieneNombre) {

        proveedoresPage = proveedorRepository
                .findByEmpresa_IdEmpresaAndEstadoAndNombreContainingIgnoreCase(
                        empresaId, estado, nombre, pageable);

    } else if (estado != null) {

        proveedoresPage = proveedorRepository
                .findByEmpresa_IdEmpresaAndEstado(
                        empresaId, estado, pageable);

    } else if (tieneNombre) {

        proveedoresPage = proveedorRepository
                .findByEmpresa_IdEmpresaAndNombreContainingIgnoreCase(
                        empresaId, nombre, pageable);

    } else {

        proveedoresPage = proveedorRepository
                .findByEmpresa_IdEmpresa(empresaId, pageable);
    }

    return proveedoresPage.map(ProveedorMapper::toDto);
}


}

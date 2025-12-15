package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.CategoriaMapper;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.CategoriaRepository;
import sigiv.Backend.sigiv.Backend.services.CategoriaService;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    public CategoriaResponseDto crearCategoria(CategoriaRequestDto dto) {
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("categoria no encontrada"));
        }
        Categoria categoria = CategoriaMapper.toEntityForCreate(dto, empresa);
        Categoria guardada = categoriaRepository.save(categoria);
        return CategoriaMapper.toDto(guardada);
    }

    @Override
    public CategoriaResponseDto obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        return CategoriaMapper.toDto(categoria);
    }

    @Override
    public CategoriaResponseDto actualizarCategoria(Long id, CategoriaRequestDto dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }

       CategoriaMapper.updateEntityFromDto(dto, categoria, empresa);
        Categoria actualizado = categoriaRepository.save(categoria);
        return CategoriaMapper.toDto(actualizado);
    }

    @Override
    public List<CategoriaResponseDto> listarCategorias() {
        return categoriaRepository.findAll()
                .stream().map(CategoriaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaResponseDto> listarPorEstado(Categoria.Estado estado) {
        return categoriaRepository.findByEstado(estado)
                .stream()
                .map(CategoriaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", "id", id);
        }
        categoriaRepository.deleteById(id);
    }

     @Override
public CategoriaResponseDto cambiarEstado(Long id) {
    Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

    // Cambiar estado automáticamente
    if (categoria.getEstado() == Categoria.Estado.Activo) {
        categoria.setEstado(Categoria.Estado.Inactivo);
    } else {
        categoria.setEstado(Categoria.Estado.Activo);
    }

    Categoria actualizado = categoriaRepository.save(categoria);
    return CategoriaMapper.toDto(actualizado);
}
@Override
public List<CategoriaResponseDto> buscarPorNombre(String nombre) {
    return categoriaRepository.findByNombreContainingIgnoreCase(nombre)
            .stream()
            .map(CategoriaMapper::toDto)
            .toList();
}

@Override
public List<CategoriaResponseDto> listarPorEmpresa(Long idEmpresa) {

    List<Categoria> categorias = categoriaRepository.findCategoriasByEmpresa(idEmpresa);

    return categorias.stream()
            .map(CategoriaMapper::toDto)
            .toList();
}

}

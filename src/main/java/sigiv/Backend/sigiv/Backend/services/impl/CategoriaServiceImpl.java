package sigiv.Backend.sigiv.Backend.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", dto.getEmpresaId()));
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
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", dto.getEmpresaId()));
        }

        CategoriaMapper.updateEntityFromDto(dto, categoria, empresa);

        Categoria actualizado = categoriaRepository.save(categoria);

        return CategoriaMapper.toDto(actualizado);
    }

    @Override
    public Page<CategoriaResponseDto> listarCategoriasPorEmpresa(
            Long empresaId,
            int page,
            int size,
            Categoria.Estado estado,
            String nombre
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("idcategoria").ascending());

        boolean tieneNombre = nombre != null && !nombre.trim().isEmpty();

        Page<Categoria> categoriasPage;

        if (estado != null && tieneNombre) {

            categoriasPage = categoriaRepository
                    .findByEmpresa_IdEmpresaAndEstadoAndNombreContainingIgnoreCase(
                            empresaId, estado, nombre, pageable);

        } else if (estado != null) {

            categoriasPage = categoriaRepository
                    .findByEmpresa_IdEmpresaAndEstado(
                            empresaId, estado, pageable);

        } else if (tieneNombre) {

            categoriasPage = categoriaRepository
                    .findByEmpresa_IdEmpresaAndNombreContainingIgnoreCase(
                            empresaId, nombre, pageable);

        } else {

            categoriasPage = categoriaRepository
                    .findByEmpresa_IdEmpresa(empresaId, pageable);
        }

        return categoriasPage.map(CategoriaMapper::toDto);
    }

    @Override
    public void eliminarCategoria(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        categoriaRepository.delete(categoria);
    }
}
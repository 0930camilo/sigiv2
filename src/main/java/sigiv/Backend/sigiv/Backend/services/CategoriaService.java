package sigiv.Backend.sigiv.Backend.services;



import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Categoria;


public interface CategoriaService {
    CategoriaResponseDto crearCategoria(CategoriaRequestDto dto);
    CategoriaResponseDto obtenerPorId(Long id);
    CategoriaResponseDto actualizarCategoria(Long id, CategoriaRequestDto dto);
    void eliminarCategoria(Long id);
    
  Page<CategoriaResponseDto> listarCategoriasPorEmpresa(
        Long empresaId,
        int page,
        int size,
        Categoria.Estado estado,
        String nombre
);

}

package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.catego.CategoriaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Categoria;

public interface CategoriaService {
    CategoriaResponseDto crearCategoria(CategoriaRequestDto dto);
    CategoriaResponseDto obtenerPorId(Long id);
    CategoriaResponseDto actualizarCategoria(Long id, CategoriaRequestDto dto);
    List<CategoriaResponseDto> listarCategorias();
    List<CategoriaResponseDto> listarPorEstado(Categoria.Estado estado);
    void eliminarCategoria(Long id);
    CategoriaResponseDto cambiarEstado(Long id);

}

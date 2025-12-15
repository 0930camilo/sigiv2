package sigiv.Backend.sigiv.Backend.dto.catego;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Categoria;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDto {

    private Long idCategoria;
    private String nombre;
    private Categoria.Estado estado;
    private Long empresaId;
}
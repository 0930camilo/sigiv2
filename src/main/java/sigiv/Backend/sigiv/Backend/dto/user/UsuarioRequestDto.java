package sigiv.Backend.sigiv.Backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Usuario;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDto {
    private String nombres;
    private String clave;
    private Long telefono;
    private String direccion;
    private Usuario.Estado estado;
    private Long empresaId;
}

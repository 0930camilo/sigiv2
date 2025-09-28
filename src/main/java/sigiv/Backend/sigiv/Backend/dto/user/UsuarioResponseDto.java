package sigiv.Backend.sigiv.Backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Usuario;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {
    private Long idUsuario;
    private String nombres;
    private String direccion;
    private Long telefono;
    private Usuario.Estado estado;
    private Long empresaId;
}

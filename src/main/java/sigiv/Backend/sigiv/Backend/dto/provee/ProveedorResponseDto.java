package sigiv.Backend.sigiv.Backend.dto.provee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponseDto {

    private Long idProveedor;
    private String nombre;
    private Long telefono;
    private String direccion;
    private Proveedor.Estado estado;
    private Long empresaId;
   
}

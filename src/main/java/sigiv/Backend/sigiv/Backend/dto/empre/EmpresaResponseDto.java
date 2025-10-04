package sigiv.Backend.sigiv.Backend.dto.empre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Empresa;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponseDto {
    private long id_Empresa;

    private String nombre_empresa;
    private String clave;
    private String nit;
    private long telefono;
    private String direccion;
    private Empresa.Estado estado;
  
}

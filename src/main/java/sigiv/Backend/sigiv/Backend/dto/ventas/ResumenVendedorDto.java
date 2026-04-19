package sigiv.Backend.sigiv.Backend.dto.ventas;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumenVendedorDto {
    private String nombreUsuario;
    private Long cantidadVentas;
    private BigDecimal totalVendido;
}

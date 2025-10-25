package sigiv.Backend.sigiv.Backend.dto.detalleCotizacion;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCotizacionResponseDto {
    private String descripcionProducto;
    private Integer cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;
}

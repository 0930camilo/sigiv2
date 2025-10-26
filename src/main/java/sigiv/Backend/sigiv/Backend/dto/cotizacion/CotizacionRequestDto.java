package sigiv.Backend.sigiv.Backend.dto.cotizacion;

import java.util.List;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionRequestDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDto {
    private Long usuarioId;
    private String nombreCliente;
    private String telefonoCliente;
    private BigDecimal total; 
    private List<DetalleCotizacionRequestDto> detalles;
}

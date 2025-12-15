package sigiv.Backend.sigiv.Backend.dto.cotizacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionResponseDto {

    private Long idcotizacion;
    private LocalDateTime fecha;
    private String nombreCliente;
    private String telefonoCliente;
    private BigDecimal total;

    private String nombreUsuario;

    private List<DetalleCotizacionResponseDto> detalles;
}

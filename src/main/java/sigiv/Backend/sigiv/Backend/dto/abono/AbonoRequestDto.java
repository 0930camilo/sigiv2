package sigiv.Backend.sigiv.Backend.dto.abono;

import java.math.BigDecimal;
import sigiv.Backend.sigiv.Backend.entity.Abono.MetodoPago;
import lombok.Data;

@Data
public class AbonoRequestDto {
    private BigDecimal valor;
    private MetodoPago metodoPago;
    private String observacion;
    private Long usuarioId;
}
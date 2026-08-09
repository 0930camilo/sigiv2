package sigiv.Backend.sigiv.Backend.dto.devol;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionRequestDto {
    private Long ventaId;
    private Long productoId;
    private BigDecimal cantidad;
    private String motivo;
}

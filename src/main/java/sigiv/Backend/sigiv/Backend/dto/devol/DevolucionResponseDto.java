package sigiv.Backend.sigiv.Backend.dto.devol;

import java.math.BigDecimal;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionResponseDto {
    private Long iddevolucion;
    private Long ventaId;
    private Long productoId;
    private String nombreProducto;
    private BigDecimal cantidad;
    private String motivo;
    private LocalDateTime fecha;
}

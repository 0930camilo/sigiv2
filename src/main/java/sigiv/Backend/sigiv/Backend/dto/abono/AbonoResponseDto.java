package sigiv.Backend.sigiv.Backend.dto.abono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import sigiv.Backend.sigiv.Backend.entity.Abono.MetodoPago;
import lombok.Data;

@Data
public class AbonoResponseDto {
    private Long idAbono;
    private Long ventaId;
    private Long usuarioId;
    private String nombreUsuario;
    private BigDecimal valor;
    private LocalDateTime fecha;
    private MetodoPago metodoPago;
    private String observacion;
}
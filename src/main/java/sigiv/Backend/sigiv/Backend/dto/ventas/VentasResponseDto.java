package sigiv.Backend.sigiv.Backend.dto.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class VentasResponseDto {
    private Long idventa;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String nombreCliente;
    private String telefonoCliente;
    private BigDecimal efectivo;
    private BigDecimal cambio;
    private String nombreUsuario; // nombre del usuario asociado
}
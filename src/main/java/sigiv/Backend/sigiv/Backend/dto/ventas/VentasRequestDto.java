package sigiv.Backend.sigiv.Backend.dto.ventas;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class VentasRequestDto {
    private LocalDateTime fecha;
    private BigDecimal total;
    private String nombreCliente;
    private String telefonoCliente;
    private BigDecimal efectivo;
    private BigDecimal cambio;
    private Long usuarioId; // para asociar con el usuario
 
}

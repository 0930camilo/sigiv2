package sigiv.Backend.sigiv.Backend.dto.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentasResponseDto {

    private Long idventa;
    private LocalDateTime fecha;
    private String nombreCliente;
    private String telefonoCliente;
    private BigDecimal total;
    private BigDecimal efectivo;
    private BigDecimal cambio;
    private String nombreUsuario;
    private List<DetalleVentaResponseDto> detalles;
}

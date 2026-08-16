package sigiv.Backend.sigiv.Backend.dto.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Ventas.EstadoPago;
import sigiv.Backend.sigiv.Backend.entity.Ventas.TipoPago;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentasResponseDto {

    private Long idventa;
    private LocalDateTime fecha;
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String documentoCliente;
    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal total;
    private BigDecimal totalAbonado;
    private BigDecimal saldoPendiente;
    private BigDecimal efectivo;
    private BigDecimal cambio;
    private String nombreUsuario;
    private Long empresaId;
    private TipoPago tipoPago;
    private EstadoPago estadoPago;
    private List<DetalleVentaResponseDto> detalles;
}
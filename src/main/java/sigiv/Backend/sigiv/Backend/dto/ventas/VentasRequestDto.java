package sigiv.Backend.sigiv.Backend.dto.ventas;

import java.math.BigDecimal;
import java.util.List;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.entity.Abono.MetodoPago;
import sigiv.Backend.sigiv.Backend.entity.Ventas.TipoPago;
import lombok.Data;

@Data
public class VentasRequestDto {
    private Long usuarioId;
    private Long empresaId;
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String documentoCliente;
    private BigDecimal descuentoTotal;
    private BigDecimal efectivo;
    private List<DetalleVentaRequestDto> detalles;

    // Campos para Crédito
    private TipoPago tipoPago;
    private BigDecimal abonoInicial;
    private MetodoPago metodoPagoAbonoInicial;
}
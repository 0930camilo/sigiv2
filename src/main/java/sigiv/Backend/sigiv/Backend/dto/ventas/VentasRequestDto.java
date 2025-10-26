package sigiv.Backend.sigiv.Backend.dto.ventas;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentasRequestDto {

    private Long usuarioId;
    private String nombreCliente;
    private String telefonoCliente;
    private BigDecimal efectivo;
    private List<DetalleVentaRequestDto> detalles;
}

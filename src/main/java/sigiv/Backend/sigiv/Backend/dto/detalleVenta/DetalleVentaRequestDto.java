package sigiv.Backend.sigiv.Backend.dto.detalleVenta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequestDto {
    private Long productoId;
    private BigDecimal cantidad;
}

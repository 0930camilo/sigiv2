package sigiv.Backend.sigiv.Backend.dto.detalleCotizacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCotizacionRequestDto {
    private Long productoId;
    private Integer cantidad;
}

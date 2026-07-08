package sigiv.Backend.sigiv.Backend.dto.ventas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnviarFacturaCorreoRequestDto {

    private String correoDestino;
    private String formatoFactura;
}

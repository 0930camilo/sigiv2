package sigiv.Backend.sigiv.Backend.dto.cotizacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnviarCotizacionCorreoRequestDto {

    private String correoDestino;
}
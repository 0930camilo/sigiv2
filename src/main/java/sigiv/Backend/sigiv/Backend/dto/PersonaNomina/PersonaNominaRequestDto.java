package sigiv.Backend.sigiv.Backend.dto.PersonaNomina;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonaNominaRequestDto {

    private Long idNomina;       // ID de la nómina
    private Long idPersona;      // ID de la persona
    private Integer diasTrabajados;
    private BigDecimal valorDia;
}

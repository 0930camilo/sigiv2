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
public class PersonaNominaResponseDto {

    private Long idNomina;
    private Long idPersona;
    private Integer diasTrabajados;
    private BigDecimal valorDia;
    private BigDecimal salario; // calculado automáticamente
}

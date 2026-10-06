package sigiv.Backend.sigiv.Backend.dto.calculadora;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglaDeTresRequestDto {
    private BigDecimal a;
    private BigDecimal b;
    private BigDecimal c;
}

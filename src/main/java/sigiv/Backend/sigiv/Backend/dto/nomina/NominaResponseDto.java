package sigiv.Backend.sigiv.Backend.dto.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Nomina;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NominaResponseDto {

  private Long idNomina;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalPago;
    private Nomina.Estado estado;
    private Long empresaId;
}

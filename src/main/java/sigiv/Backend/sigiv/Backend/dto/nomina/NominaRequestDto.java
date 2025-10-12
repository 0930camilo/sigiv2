package sigiv.Backend.sigiv.Backend.dto.nomina;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Nomina;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NominaRequestDto {
    private Long idNomina;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Nomina.Estado estado;
    private Long empresaId; // 🔹 totalPago se calcula automáticamente
}

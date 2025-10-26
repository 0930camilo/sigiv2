package sigiv.Backend.sigiv.Backend.dto.devol;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionRequestDto {
    private Long ventaId;
    private Long productoId;
    private Integer cantidad;
    private String motivo;
}

package sigiv.Backend.sigiv.Backend.dto.correo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorreoEmpresaResponseDto {

    private Long empresaId;
    private String correo;
    private String smtpHost;
    private Integer smtpPort;
    private Boolean startTls;
    private Boolean configurado;
    private LocalDateTime actualizadoEn;
}

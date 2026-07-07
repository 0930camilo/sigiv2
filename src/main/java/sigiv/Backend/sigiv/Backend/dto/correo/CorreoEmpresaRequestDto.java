package sigiv.Backend.sigiv.Backend.dto.correo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorreoEmpresaRequestDto {

    private String correo;
    private String claveAplicacion;
    private String smtpHost;
    private Integer smtpPort;
    private Boolean startTls;
}

package sigiv.Backend.sigiv.Backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "correo_empresa")
@Getter
@Setter
@NoArgsConstructor
public class CorreoEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCorreoEmpresa;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_idempresa", nullable = false, unique = true)
    private Empresa empresa;

    private String claveAplicacion;
    private String smtpHost;
    private Integer smtpPort;
    private Boolean startTls;
    private LocalDateTime actualizadoEn;
}

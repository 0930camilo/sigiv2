package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "persona_nomina")
@IdClass(PersonaNominaId.class)
public class PersonaNomina {

    @Id
    private Long idnomina;

    @Id
    private Long idpersona;

    @Column(name = "dias_trabajados")
    private Integer diasTrabajados;

    @Column(name = "valor_dia")
    private BigDecimal valorDia;

    @Column(name = "salario")
    private BigDecimal salario;

    // Relaciones opcionales
    @ManyToOne
    @JoinColumn(name = "idpersona", insertable = false, updatable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name = "idnomina", insertable = false, updatable = false)
    private Nomina nomina;

    // ---- Cálculo automático del salario ----
    @PrePersist
    @PreUpdate
    public void calcularSalario() {
        if (diasTrabajados != null && valorDia != null) {
            this.salario = valorDia.multiply(BigDecimal.valueOf(diasTrabajados));
        } else {
            this.salario = BigDecimal.ZERO;
        }
    }
}

package sigiv.Backend.sigiv.Backend.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(
        name = "Persona",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"empresa_idempresa", "documento"})
        }
)
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpersona;

    @Column(nullable = false)
    private String documento;

    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private LocalDate fechaIngreso;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    public enum Estado {
        Activo, Inactivo
    }

    @ManyToOne
    @JoinColumn(name = "empresa_idempresa")
    @JsonBackReference("empresa-personas")
    private Empresa empresa;

}
package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "nomina") // mejor minúscula por convención
@Data
public class Nomina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnomina;

    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalPago;

    @ManyToOne
    @JoinColumn(name = "empresa_idempresa")
    @JsonBackReference("empresa-nominas")
    private Empresa empresa;
}

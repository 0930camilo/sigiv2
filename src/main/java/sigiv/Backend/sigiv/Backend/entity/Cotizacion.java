package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Cotizacion")
@Data
public class Cotizacion {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcotizacion;

    private String nombreCliente;
    private String telefonoCliente;
    private LocalDateTime fecha;
    private BigDecimal total;

 @ManyToOne
@JoinColumn(name = "usuario_idusuario")
@JsonBackReference("usuario-cotizaciones")
private Usuario usuario;

}

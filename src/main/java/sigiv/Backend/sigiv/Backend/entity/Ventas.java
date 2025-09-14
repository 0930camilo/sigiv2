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
@Data
@Table(name = "Ventas")
public class Ventas {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idventa;

    private LocalDateTime fecha;
    private BigDecimal total;
    private String nombreCliente;
    private String telefonoCliente;
    private BigDecimal efectivo;
    private BigDecimal cambio;

   @ManyToOne
@JoinColumn(name = "usuario_idusuario")
@JsonBackReference("usuario-ventas")
private Usuario usuario;
}

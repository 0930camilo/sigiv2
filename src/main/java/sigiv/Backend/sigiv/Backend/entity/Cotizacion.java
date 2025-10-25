package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
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
    @JsonBackReference("usuario-cotizacion")
    private Usuario usuario;

    // ✅ Relación con los detalles de cotización (ahora con fetch EAGER)
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("cotizacion-detalles")
    private List<DetalleCotizacion> detalles;
}

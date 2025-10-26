package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ventas")
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

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleVentas> detalles;
}

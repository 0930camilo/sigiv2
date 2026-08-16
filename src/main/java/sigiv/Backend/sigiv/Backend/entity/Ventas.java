package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal total;
    private String nombreCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String documentoCliente;
    private BigDecimal efectivo;
    private BigDecimal cambio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'CONTADO'")
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'PAGADA'")
    private EstadoPago estadoPago;

    @ManyToOne
    @JoinColumn(name = "usuario_idusuario")
    @JsonBackReference("usuario-ventas")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "empresa_idempresa")
    @JsonBackReference("empresa-ventas")
    private Empresa empresa;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetalleVentas> detalles;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("venta-abonos")
    private List<Abono> abonos = new ArrayList<>();

    public enum TipoPago {
        CONTADO,
        CREDITO
    }

    public enum EstadoPago {
        PAGADA,
        PENDIENTE
    }
}
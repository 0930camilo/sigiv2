package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import lombok.Data;

@Entity
@Data
@Table(name = "abonos")
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAbono;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    @JsonBackReference("venta-abonos")
    private Ventas venta;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference("usuario-abonos")
    private Usuario usuario;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    private String observacion;

    public enum MetodoPago {
        EFECTIVO,
        TRANSFERENCIA,
        TARJETA_DEBITO,
        TARJETA_CREDITO,
        OTRO
    }
}
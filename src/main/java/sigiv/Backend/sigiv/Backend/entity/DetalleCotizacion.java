package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detalle_cotizacion")
public class DetalleCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddetalle;

    private Integer cantidad;

    private BigDecimal precio;

    // ✅ CAMBIADO: ahora es una columna normal, sin "generated always as"
    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal;

    private String descripcionProducto;

    @ManyToOne
    @JoinColumn(name = "cotizacion_idcotizacion")
    @JsonBackReference("cotizacion-detalles")
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "producto_idproducto")
    private Producto producto;
}

package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detalle_ventas")
public class DetalleVentas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddetalle;

    private Integer cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;
    private String descripcionProducto;

    @ManyToOne
    @JoinColumn(name = "venta_idventa")
    @JsonBackReference
    private Ventas venta;

    @ManyToOne
    @JoinColumn(name = "producto_idproducto")
    private Producto producto;
}

package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
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
@Table(name = "detalle_ventas")
public class DetalleVentas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iddetalle;

    private Integer cantidad;
    private BigDecimal precio;

    @Column(columnDefinition = "decimal(15,2) generated always as (cantidad * precio) stored")
    private BigDecimal subtotal;

    private String descripcionProducto;

    @ManyToOne
    @JoinColumn(name = "venta_idventa")
    private Ventas venta;

    @ManyToOne
    @JoinColumn(name = "producto_idproducto")
    private Producto producto;
}

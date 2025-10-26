package sigiv.Backend.sigiv.Backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDevolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_idventa", nullable = false)
    private Ventas venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_idproducto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha;
}

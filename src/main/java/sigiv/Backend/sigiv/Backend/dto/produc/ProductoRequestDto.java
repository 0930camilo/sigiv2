package sigiv.Backend.sigiv.Backend.dto.produc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sigiv.Backend.sigiv.Backend.entity.Producto;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDto {
     private Long idProducto;
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioCompra;
    private BigDecimal precio;
    private LocalDateTime fecha;
    private Producto.Estado estado;
    private Long proveedorId;
    private Long categoriaId;
}

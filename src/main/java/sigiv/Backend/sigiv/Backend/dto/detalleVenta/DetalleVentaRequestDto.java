package sigiv.Backend.sigiv.Backend.dto.detalleVenta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequestDto {
    private Long productoId;          // id del producto seleccionado
    private Integer cantidad;         // cantidad a vender
    private String descripcionProducto; // opcional, si quieres sobreescribir descripción
}

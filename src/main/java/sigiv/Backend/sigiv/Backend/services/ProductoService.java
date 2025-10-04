package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import org.springframework.security.access.method.P;

import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;

public interface ProductoService {
    ProductoResponseDto crearProducto(ProductoRequestDto     dto);
    ProductoResponseDto obtenerPorId(Long id);
    ProductoResponseDto actualizarProducto(Long id, ProductoRequestDto dto);
    List<ProductoResponseDto> listarProductos();
    List<ProductoResponseDto> listarPorEstado(Producto.Estado estado);
    void eliminarProducto(Long id);
    ProductoResponseDto cambiarEstado(Long id);

}

package sigiv.Backend.sigiv.Backend.services;





import java.io.InputStream;

import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.produc.ProductoImportResultDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;


public interface ProductoService {
    ProductoResponseDto crearProducto(ProductoRequestDto     dto);
    ProductoResponseDto obtenerPorId(Long id);
    ProductoResponseDto obtenerPorCodigoBarra(String codigoBarra);
    String obtenerCodigoBarraBase64PorId(Long id);
    String obtenerCodigoBarraBase64PorCodigo(String codigoBarra);
    ProductoResponseDto actualizarProducto(Long id, ProductoRequestDto dto);
    void eliminarProducto(Long id);


    
Page<ProductoResponseDto> productosPorEmpresa(
        Long idEmpresa,
        int page,
        int size,
        Producto.Estado estado,
        String nombre,
        String categoria,
        String proveedor
);

    ProductoImportResultDto importarDesdeExcel(InputStream inputStream);

    byte[] generarPlantillaExcel(Long empresaId) throws Exception;
}

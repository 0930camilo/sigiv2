package sigiv.Backend.sigiv.Backend.services.impl;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.ProductoMapper;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.repository.ProveedorRepository;
import sigiv.Backend.sigiv.Backend.repository.CategoriaRepository;
import sigiv.Backend.sigiv.Backend.services.ProductoService;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;

    // Crear producto
    @Override
    public ProductoResponseDto crearProducto(ProductoRequestDto dto) {
        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Producto producto = ProductoMapper.toEntityForCreate(dto, proveedor, categoria);
        Producto guardado = productoRepository.save(producto);
        return ProductoMapper.toDto(guardado);
    }

    // Obtener producto por ID
    @Override
    public ProductoResponseDto obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return ProductoMapper.toDto(producto);
    }

    // Actualizar producto
    @Override
    public ProductoResponseDto actualizarProducto(Long id, ProductoRequestDto dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        Proveedor proveedor = null;
        if (dto.getProveedorId() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        }

        Categoria categoria = null;
        if (dto.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        }

        ProductoMapper.updateEntityFromDto(dto, producto, proveedor, categoria);
        Producto actualizado = productoRepository.save(producto);
        return ProductoMapper.toDto(actualizado);
    }



    // Eliminar producto
    @Override
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", "id", id);
        }
        productoRepository.deleteById(id);
    }

  
@Override
public Page<ProductoResponseDto> productosPorEmpresa(
        Long idEmpresa,
        int page,
        int size,
        Producto.Estado estado,
        String nombre,
        String categoria,
        String proveedor
) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("idproducto").ascending());

    Page<Producto> productosPage =
            productoRepository.buscarProductosConFiltros(
                    idEmpresa,
                    estado,
                    nombre,
                    categoria,
                    proveedor,
                    pageable
            );

    return productosPage.map(ProductoMapper::toDto);
}
}

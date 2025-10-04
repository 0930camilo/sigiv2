package sigiv.Backend.sigiv.Backend.dto.mapper;

import sigiv.Backend.sigiv.Backend.dto.produc.ProductoRequestDto;
import sigiv.Backend.sigiv.Backend.dto.produc.ProductoResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;

public class ProductoMapper {

    public static ProductoResponseDto toDto(Producto p) {
        if (p == null) return null;
        return new ProductoResponseDto(
            p.getIdproducto(),
            p.getNombre(),
            p.getDescripcion(),
            p.getCantidad(),
            p.getPrecioCompra(),
            p.getPrecio(),
            p.getFecha(),
            p.getEstado(),
            p.getProveedor() != null ? p.getProveedor().getIdproveedor() : null,
            p.getCategoria() != null ? p.getCategoria().getIdcategoria() : null
        );
    }

    // Crear nuevo producto
    public static Producto toEntityForCreate(ProductoRequestDto dto, Proveedor proveedor, Categoria categoria) {
        Producto p = new Producto();
        updateEntityFromDto(dto, p, proveedor, categoria);
        return p;
    }

    // Actualizar producto existente
    public static void updateEntityFromDto(ProductoRequestDto dto, Producto entity, Proveedor proveedor, Categoria categoria) {
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());

        if (dto.getDescripcion() != null) entity.setDescripcion(dto.getDescripcion());
        if (dto.getCantidad() != null) entity.setCantidad(dto.getCantidad());
        if (dto.getPrecioCompra() != null) entity.setPrecioCompra(dto.getPrecioCompra());
        if (dto.getPrecio() != null) entity.setPrecio(dto.getPrecio());
        if (dto.getFecha() != null) entity.setFecha(dto.getFecha());
        if (dto.getEstado() != null) entity.setEstado(dto.getEstado());
        if (proveedor != null) entity.setProveedor(proveedor);
        if (categoria != null) entity.setCategoria(categoria);
    }
}

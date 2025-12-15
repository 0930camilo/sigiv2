package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByEstado(Producto.Estado estado);

    List<Producto> findByCategoria_Idcategoria(Long idCategoria);

    List<Producto> findByProveedor_Idproveedor(Long idproveedor);

    List<Producto> findByCategoria_Empresa_IdEmpresa(Long idEmpresa);

    List<Producto> findByProveedor_Empresa_IdEmpresa(Long idEmpresa);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
}

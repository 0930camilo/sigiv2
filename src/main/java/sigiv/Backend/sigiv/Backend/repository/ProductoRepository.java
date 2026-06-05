package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sigiv.Backend.sigiv.Backend.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEstado(Producto.Estado estado);

    List<Producto> findByCategoria_Idcategoria(Long idCategoria);

    List<Producto> findByProveedor_Idproveedor(Long idproveedor);

    List<Producto> findByCategoria_Empresa_IdEmpresa(Long idEmpresa);

    List<Producto> findByProveedor_Empresa_IdEmpresa(Long idEmpresa);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    Optional<Producto> findByCodigoBarra(String codigoBarra);

    boolean existsByCodigoBarra(String codigoBarra);

    @Query("""
    SELECT p FROM Producto p
    LEFT JOIN p.categoria c
    LEFT JOIN p.proveedor pr
    WHERE
    (
       (c IS NOT NULL AND c.empresa.idEmpresa = :idEmpresa)
       OR
       (pr IS NOT NULL AND pr.empresa.idEmpresa = :idEmpresa)
    )
    AND (:estado IS NULL OR p.estado = :estado)
    AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
    AND (:categoria IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%')))
    AND (:proveedor IS NULL OR LOWER(pr.nombre) LIKE LOWER(CONCAT('%', :proveedor, '%')))
    AND (:codigoBarra IS NULL OR p.codigoBarra = :codigoBarra)
    """)
    Page<Producto> buscarProductosConFiltros(
            @Param("idEmpresa") Long idEmpresa,
            @Param("estado") Producto.Estado estado,
            @Param("nombre") String nombre,
            @Param("categoria") String categoria,
            @Param("proveedor") String proveedor,
            @Param("codigoBarra") String codigoBarra,
            Pageable pageable
    );
}
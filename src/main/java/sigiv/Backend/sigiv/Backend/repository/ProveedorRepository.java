package sigiv.Backend.sigiv.Backend.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import sigiv.Backend.sigiv.Backend.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

  
Page<Proveedor> findByEmpresa_IdEmpresa(Long empresaId, Pageable pageable);

Page<Proveedor> findByEmpresa_IdEmpresaAndEstado(
        Long empresaId,
        Proveedor.Estado estado,
        Pageable pageable
);

Page<Proveedor> findByEmpresa_IdEmpresaAndNombreContainingIgnoreCase(
        Long empresaId,
        String nombre,
        Pageable pageable
);

Page<Proveedor> findByEmpresa_IdEmpresaAndEstadoAndNombreContainingIgnoreCase(
        Long empresaId,
        Proveedor.Estado estado,
        String nombre,
        Pageable pageable
);

Page<Proveedor> findByEmpresa_IdEmpresaAndDocumentoContainingIgnoreCase(
        Long empresaId,
        String documento,
        Pageable pageable
);

Page<Proveedor> findByEmpresa_IdEmpresaAndEstadoAndDocumentoContainingIgnoreCase(
        Long empresaId,
        Proveedor.Estado estado,
        String documento,
        Pageable pageable
);

Page<Proveedor> findByEmpresa_IdEmpresaAndNombreContainingIgnoreCaseAndDocumentoContainingIgnoreCase(
        Long empresaId,
        String nombre,
        String documento,
        Pageable pageable
);

Page<Proveedor> findByEmpresa_IdEmpresaAndEstadoAndNombreContainingIgnoreCaseAndDocumentoContainingIgnoreCase(
        Long empresaId,
        Proveedor.Estado estado,
        String nombre,
        String documento,
        Pageable pageable
);

}
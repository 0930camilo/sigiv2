package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import sigiv.Backend.sigiv.Backend.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
        List<Proveedor> findByEstado(Proveedor.Estado estado);

     List<Proveedor> findByNombreContainingIgnoreCase(String nombre);



      @Query("""
    SELECT p
    FROM Proveedor p
    WHERE p.empresa.idEmpresa = :idEmpresa
""")
List<Proveedor> findProveedoresByEmpresa(@Param("idEmpresa") Long idEmpresa);
    
}

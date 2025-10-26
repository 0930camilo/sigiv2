package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sigiv.Backend.sigiv.Backend.entity.Categoria;
import sigiv.Backend.sigiv.Backend.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
        List<Proveedor> findByEstado(Proveedor.Estado estado);

    
    
}

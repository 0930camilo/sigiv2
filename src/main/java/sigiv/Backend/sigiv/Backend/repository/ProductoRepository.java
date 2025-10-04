package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
     List<Producto> findByEstado(Producto.Estado estado);
    
}

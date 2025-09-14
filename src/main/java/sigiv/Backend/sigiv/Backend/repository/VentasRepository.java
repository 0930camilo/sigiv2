package sigiv.Backend.sigiv.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VentasRepository extends JpaRepository<sigiv.Backend.sigiv.Backend.entity.Ventas, Long> {
    
    // Additional query methods can be defined here if needed
    
}

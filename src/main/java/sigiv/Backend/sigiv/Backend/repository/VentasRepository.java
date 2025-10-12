package sigiv.Backend.sigiv.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigiv.Backend.sigiv.Backend.entity.Ventas;

public interface VentasRepository extends JpaRepository<Ventas, Long> {
}

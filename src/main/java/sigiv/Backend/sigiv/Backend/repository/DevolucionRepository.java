package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigiv.Backend.sigiv.Backend.entity.Devolucion;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    List<Devolucion> findByVentaIdventa(Long ventaId);
}

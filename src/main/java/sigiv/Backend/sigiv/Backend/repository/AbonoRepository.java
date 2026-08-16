package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sigiv.Backend.sigiv.Backend.entity.Abono;

public interface AbonoRepository extends JpaRepository<Abono, Long> {
    List<Abono> findByVentaIdventa(Long ventaId);
}
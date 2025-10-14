package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sigiv.Backend.sigiv.Backend.entity.DetalleVentas;

public interface DetalleVentaRepository extends JpaRepository<DetalleVentas, Long> {
    List<DetalleVentas> findByVentaIdventa(Long ventaId);
}

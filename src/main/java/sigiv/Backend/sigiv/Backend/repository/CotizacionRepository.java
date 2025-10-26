package sigiv.Backend.sigiv.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sigiv.Backend.sigiv.Backend.entity.Cotizacion;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
}

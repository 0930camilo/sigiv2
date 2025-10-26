package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {
  List<DetalleCotizacion> findByCotizacion_Idcotizacion(Long cotizacionId);

}

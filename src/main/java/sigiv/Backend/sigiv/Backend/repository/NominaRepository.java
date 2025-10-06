package sigiv.Backend.sigiv.Backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import sigiv.Backend.sigiv.Backend.entity.Nomina;

public interface NominaRepository extends JpaRepository<Nomina, Long> {
  List<Nomina> findByEstado(Nomina.Estado estado);
}

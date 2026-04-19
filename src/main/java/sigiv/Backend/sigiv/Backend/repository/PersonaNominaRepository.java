package sigiv.Backend.sigiv.Backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sigiv.Backend.sigiv.Backend.entity.PersonaNomina;
import sigiv.Backend.sigiv.Backend.entity.PersonaNominaId;

public interface PersonaNominaRepository extends JpaRepository<PersonaNomina, PersonaNominaId> {
    
       @Query("SELECT COALESCE(SUM(pn.diasTrabajados * pn.valorDia), 0) " +
           "FROM PersonaNomina pn WHERE pn.nomina.idnomina = :idnomina")
    BigDecimal calcularTotalPorNomina(@Param("idnomina") Long idnomina);

    List<PersonaNomina> findByIdnomina(Long idnomina);
    
}

package sigiv.Backend.sigiv.Backend.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface PersonaNominaRepository extends JpaRepository<sigiv.Backend.sigiv.Backend.entity.PersonaNomina, sigiv.Backend.sigiv.Backend.entity.PersonaNominaId> {
    
       @Query("SELECT COALESCE(SUM(pn.diasTrabajados * pn.valorDia), 0) " +
           "FROM PersonaNomina pn WHERE pn.nomina.idnomina = :idnomina")
    BigDecimal calcularTotalPorNomina(@Param("idnomina") Long idnomina);
    
}

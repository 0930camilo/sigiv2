package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sigiv.Backend.sigiv.Backend.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    List<Persona> findByEstado(Persona.Estado estado);
    List<Persona> findByEmpresaIdEmpresa(Long empresaId);
    Page<Persona> findByEmpresaIdEmpresa(Long empresaId, Pageable pageable);
    List<Persona> findByEmpresaIdEmpresaAndEstado(Long empresaId, Persona.Estado estado);

    @Query("""
        SELECT p FROM Persona p
        WHERE p.empresa.idEmpresa = :empresaId
        AND (:estado IS NULL OR p.estado = :estado)
        AND (:documento IS NULL OR p.documento LIKE CONCAT('%', :documento, '%'))
        AND (:nombre IS NULL OR p.nombre LIKE CONCAT('%', :nombre, '%'))
    """)
    Page<Persona> filtrarPorEmpresa(
        @Param("empresaId") Long empresaId,
        @Param("estado") Persona.Estado estado,
        @Param("documento") String documento,
        @Param("nombre") String nombre,
        Pageable pageable
    );
}

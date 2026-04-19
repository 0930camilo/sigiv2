package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    List<Persona> findByEstado(Persona.Estado estado);
    List<Persona> findByEmpresaIdEmpresa(Long empresaId);
    Page<Persona> findByEmpresaIdEmpresa(Long empresaId, Pageable pageable);
    List<Persona> findByEmpresaIdEmpresaAndEstado(Long empresaId, Persona.Estado estado);
}

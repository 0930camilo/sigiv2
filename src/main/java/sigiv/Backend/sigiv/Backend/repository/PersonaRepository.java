package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    List<Persona> findByEstado(Persona.Estado estado);
    
}

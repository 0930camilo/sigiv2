package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sigiv.Backend.sigiv.Backend.entity.Usuario;

@Repository
public interface UsuarioRepository  extends JpaRepository<Usuario, Long> {
    List<Usuario> findByEstado(Usuario.Estado estado);
    Optional<Usuario> findByNombres(String nombres);
}

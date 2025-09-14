package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.Usuario;

public interface UsuarioRepository  extends JpaRepository<sigiv.Backend.sigiv.Backend.entity.Usuario, Long> {
    List<Usuario> findByEstado(Usuario.Estado estado);
}

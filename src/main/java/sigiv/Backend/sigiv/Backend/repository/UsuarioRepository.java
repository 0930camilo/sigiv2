package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sigiv.Backend.sigiv.Backend.entity.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

 
    Optional<Usuario> findByNombres(String nombres);

    Page<Usuario> findByEmpresa_IdEmpresa(Long idEmpresa, Pageable pageable);
    

    Page<Usuario> findByEmpresa_IdEmpresaAndEstado(
            Long idEmpresa,
            Usuario.Estado estado,
            Pageable pageable
    );


    Page<Usuario> findByEmpresa_IdEmpresaAndNombresContainingIgnoreCase(
        Long empresaId,
        String nombres,
        Pageable pageable
);

Page<Usuario> findByEmpresa_IdEmpresaAndEstadoAndNombresContainingIgnoreCase(
        Long empresaId,
        Usuario.Estado estado,
        String nombres,
        Pageable pageable
);

Page<Usuario> findByEmpresa_IdEmpresaAndDocumentoContainingIgnoreCase(
        Long empresaId,
        String documento,
        Pageable pageable
);

Page<Usuario> findByEmpresa_IdEmpresaAndEstadoAndDocumentoContainingIgnoreCase(
        Long empresaId,
        Usuario.Estado estado,
        String documento,
        Pageable pageable
);

Page<Usuario> findByEmpresa_IdEmpresaAndNombresContainingIgnoreCaseAndDocumentoContainingIgnoreCase(
        Long empresaId,
        String nombres,
        String documento,
        Pageable pageable
);

Page<Usuario> findByEmpresa_IdEmpresaAndEstadoAndNombresContainingIgnoreCaseAndDocumentoContainingIgnoreCase(
        Long empresaId,
        Usuario.Estado estado,
        String nombres,
        String documento,
        Pageable pageable
);

    List<Usuario> findByNombresContainingIgnoreCase(String nombres);
}

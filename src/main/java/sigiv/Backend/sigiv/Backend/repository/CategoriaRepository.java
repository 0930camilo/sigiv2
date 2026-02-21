package sigiv.Backend.sigiv.Backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sigiv.Backend.sigiv.Backend.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Page<Categoria> findByEmpresa_IdEmpresa(Long empresaId, Pageable pageable);

    Page<Categoria> findByEmpresa_IdEmpresaAndEstado(
            Long empresaId,
            Categoria.Estado estado,
            Pageable pageable
    );

    Page<Categoria> findByEmpresa_IdEmpresaAndNombreContainingIgnoreCase(
            Long empresaId,
            String nombre,
            Pageable pageable
    );

    Page<Categoria> findByEmpresa_IdEmpresaAndEstadoAndNombreContainingIgnoreCase(
            Long empresaId,
            Categoria.Estado estado,
            String nombre,
            Pageable pageable
    );
}
package sigiv.Backend.sigiv.Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.CorreoEmpresa;

public interface CorreoEmpresaRepository extends JpaRepository<CorreoEmpresa, Long> {

    Optional<CorreoEmpresa> findByEmpresaIdEmpresa(Long empresaId);
}

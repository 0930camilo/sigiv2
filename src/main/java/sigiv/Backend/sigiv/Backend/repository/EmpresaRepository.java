package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.Empresa;

public interface EmpresaRepository extends JpaRepository<sigiv.Backend.sigiv.Backend.entity.Empresa, Long>{
    
  List<Empresa> findByEstado(Empresa.Estado estado);
}

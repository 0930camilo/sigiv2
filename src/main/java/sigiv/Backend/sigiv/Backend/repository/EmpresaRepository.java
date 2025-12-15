package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sigiv.Backend.sigiv.Backend.entity.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long>{
    
  List<Empresa> findByEstado(Empresa.Estado estado);
  Optional<Empresa> findByNombreEmpresa(String nombreEmpresa);


   
}


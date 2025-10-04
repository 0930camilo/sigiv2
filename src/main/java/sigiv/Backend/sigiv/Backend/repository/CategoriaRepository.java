package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sigiv.Backend.sigiv.Backend.entity.Categoria;



public interface CategoriaRepository  extends JpaRepository<Categoria, Long> {
   List<Categoria> findByEstado(Categoria.Estado estado);
}

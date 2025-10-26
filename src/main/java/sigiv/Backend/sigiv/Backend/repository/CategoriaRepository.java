package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sigiv.Backend.sigiv.Backend.entity.Categoria;



public interface CategoriaRepository  extends JpaRepository<Categoria, Long> {

   List<Categoria> findByEstado(Categoria.Estado estado);
   
     // Obtiene todas las categorías de la empresa a la que pertenece un usuario
    @Query("""
        SELECT c
        FROM Categoria c
        WHERE c.empresa.idEmpresa = (
            SELECT u.empresa.idEmpresa
            FROM Usuario u
            WHERE u.idUsuario = :idUsuario
        )
    """)
    List<Categoria> findCategoriasByUsuario(@Param("idUsuario") Long idUsuario);

    
}

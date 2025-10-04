package sigiv.Backend.sigiv.Backend.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Data
@Table(name = "Categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcategoria;

    private String nombre;

    
   @Enumerated(EnumType.STRING)
    private Estado estado;


     // Enum declarado fuera de los atributos
    public enum Estado {
        Activo,
        Inactivo
    }

    
  @ManyToOne
@JoinColumn(name = "empresa_idempresa")
@JsonBackReference("empresa-categorias")
private Empresa empresa;



 @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
@JsonIgnore
private List<Producto> productos;
}

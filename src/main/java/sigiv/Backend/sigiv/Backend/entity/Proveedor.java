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
@Table(name = "Proveedor")
@Data
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idproveedor;

    private String nombre;
    private long telefono;
    private String direccion;

 @Enumerated(EnumType.STRING)
    private Estado estado;


     // Enum declarado fuera de los atributos
    public enum Estado {
        Activo,
        Inactivo
    }

@ManyToOne
@JoinColumn(name = "empresa_idempresa")
@JsonBackReference("empresa-proveedores")
private Empresa empresa;


@OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
@JsonIgnore
private List<Producto> productos;



}

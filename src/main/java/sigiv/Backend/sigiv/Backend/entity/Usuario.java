package sigiv.Backend.sigiv.Backend.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuario")  // Por convención, nombres de tablas en minúscula
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUsuario;
    private String nombres;
    private String clave;
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
    @JoinColumn(name = "empresa_id")
    @JsonBackReference("empresa-usuarios")
    private Empresa empresa;

    



@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference("usuario-cotizaciones")
private List<Cotizacion> cotizacion;


@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference("usuario-ventas")
private List<Ventas> ventas;

}

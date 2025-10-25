package sigiv.Backend.sigiv.Backend.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "empresa")
@Data
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_Empresa;

    private String Nombre_empresa;
    private String clave;
    private String nit;
    private long telefono;
    private String direccion;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("empresa-usuarios")
    private List<Usuario> usuarios;

    public enum Estado {
        Activo,
        Inactivo
    }

@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference("empresa-categorias")
private List<Categoria> categorias;

@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference("empresa-proveedores")
private List<Proveedor> proveedores;

 @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference("empresa-nominas")
private List<Nomina> nominas;



@OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference("empresa-personas")
private List<Persona> persona;



}

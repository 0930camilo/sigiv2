package sigiv.Backend.sigiv.Backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "Producto")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idproducto;

    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioCompra;
    private BigDecimal precio;
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
private Estado estado;

public enum Estado {
    Activo, Inactivo
}


@ManyToOne
@JoinColumn(name = "usuario_idusuario")
@JsonBackReference("usuario-productos")
private Usuario usuario;


@ManyToOne
@JoinColumn(name = "proveedor_idproveedor")
private Proveedor proveedor;

@ManyToOne
@JoinColumn(name = "categoria_idcategoria")
private Categoria categoria;
}


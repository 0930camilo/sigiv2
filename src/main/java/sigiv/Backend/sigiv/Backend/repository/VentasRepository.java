package sigiv.Backend.sigiv.Backend.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sigiv.Backend.sigiv.Backend.entity.Ventas;

public interface VentasRepository extends JpaRepository<Ventas, Long> {


    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Ventas v WHERE v.usuario.idUsuario = :idUsuario")
    BigDecimal totalVendidoPorUsuario(@Param("idUsuario") Long idUsuario);

@Query("""
    SELECT COALESCE(SUM(v.total), 0)
    FROM Ventas v
    WHERE v.usuario.idUsuario = :idUsuario
    AND v.fecha BETWEEN :fechaInicio AND :fechaFin
""")
BigDecimal totalVendidoPorUsuarioEntreFechas(
    @Param("idUsuario") Long idUsuario,
    @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
    @Param("fechaFin") java.time.LocalDateTime fechaFin
);




@Query("""
    SELECT COALESCE(SUM(v.total), 0)
    FROM Ventas v
    WHERE v.usuario.empresa.idEmpresa = :idEmpresa
""")
BigDecimal totalVendidoPorEmpresa(@Param("idEmpresa") Long idEmpresa);


@Query("""
    SELECT COALESCE(SUM(v.total), 0)
    FROM Ventas v
    WHERE v.usuario.empresa.idEmpresa = :idEmpresa
    AND v.fecha BETWEEN :fechaInicio AND :fechaFin
""")
BigDecimal totalVendidoPorEmpresaEntreFechas(
    @Param("idEmpresa") Long idEmpresa,
    @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
    @Param("fechaFin") java.time.LocalDateTime fechaFin
);




@Query("""
        SELECT COALESCE(SUM(
            (p.precio - p.precioCompra) * dv.cantidad
        ), 0)
        FROM Ventas v
        JOIN v.detalles dv
        JOIN dv.producto p
        WHERE v.usuario.idUsuario = :idUsuario
    """)
    BigDecimal gananciaPorUsuario(@Param("idUsuario") Long idUsuario);

    // 🔹 Ganancia total por empresa
    @Query("""
        SELECT COALESCE(SUM(
            (p.precio - p.precioCompra) * dv.cantidad
        ), 0)
        FROM Ventas v
        JOIN v.detalles dv
        JOIN dv.producto p
        WHERE v.usuario.empresa.idEmpresa = :idEmpresa
    """)
    BigDecimal gananciaPorEmpresa(@Param("idEmpresa") Long idEmpresa);


}

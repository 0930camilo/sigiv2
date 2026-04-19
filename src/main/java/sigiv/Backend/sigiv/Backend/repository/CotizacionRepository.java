package sigiv.Backend.sigiv.Backend.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sigiv.Backend.sigiv.Backend.entity.Cotizacion;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    // Listar todas las cotizaciones de una empresa
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId")
    Page<Cotizacion> findByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);

    // Filtrar por empresa y usuario
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId AND c.usuario.idUsuario = :usuarioId")
    Page<Cotizacion> findByEmpresaIdAndUsuarioId(
            @Param("empresaId") Long empresaId,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    // Filtrar por empresa y nombre cliente
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId AND LOWER(c.nombreCliente) LIKE LOWER(CONCAT('%', :nombreCliente, '%'))")
    Page<Cotizacion> findByEmpresaIdAndNombreClienteContaining(
            @Param("empresaId") Long empresaId,
            @Param("nombreCliente") String nombreCliente,
            Pageable pageable
    );

    // Filtrar por empresa y rango de fechas
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId AND c.fecha >= :fechaInicio AND c.fecha <= :fechaFin")
    Page<Cotizacion> findByEmpresaIdAndFechaRange(
            @Param("empresaId") Long empresaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // Filtrar por empresa, usuario y nombre cliente
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId AND c.usuario.idUsuario = :usuarioId AND LOWER(c.nombreCliente) LIKE LOWER(CONCAT('%', :nombreCliente, '%'))")
    Page<Cotizacion> findByEmpresaIdAndUsuarioIdAndNombreClienteContaining(
            @Param("empresaId") Long empresaId,
            @Param("usuarioId") Long usuarioId,
            @Param("nombreCliente") String nombreCliente,
            Pageable pageable
    );

    // Filtrar por empresa, usuario y rango de fechas
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId AND c.usuario.idUsuario = :usuarioId AND c.fecha >= :fechaInicio AND c.fecha <= :fechaFin")
    Page<Cotizacion> findByEmpresaIdAndUsuarioIdAndFechaRange(
            @Param("empresaId") Long empresaId,
            @Param("usuarioId") Long usuarioId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // Filtrar por empresa, nombre cliente y rango de fechas
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId AND LOWER(c.nombreCliente) LIKE LOWER(CONCAT('%', :nombreCliente, '%')) AND c.fecha >= :fechaInicio AND c.fecha <= :fechaFin")
    Page<Cotizacion> findByEmpresaIdAndNombreClienteContainingAndFechaRange(
            @Param("empresaId") Long empresaId,
            @Param("nombreCliente") String nombreCliente,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // Filtrar por empresa, usuario, nombre cliente y rango de fechas (combinación completa)
    @Query("SELECT c FROM Cotizacion c WHERE c.usuario.empresa.idEmpresa = :empresaId AND c.usuario.idUsuario = :usuarioId AND LOWER(c.nombreCliente) LIKE LOWER(CONCAT('%', :nombreCliente, '%')) AND c.fecha >= :fechaInicio AND c.fecha <= :fechaFin")
    Page<Cotizacion> findByEmpresaIdAndUsuarioIdAndNombreClienteContainingAndFechaRange(
            @Param("empresaId") Long empresaId,
            @Param("usuarioId") Long usuarioId,
            @Param("nombreCliente") String nombreCliente,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );
}

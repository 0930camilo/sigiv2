package sigiv.Backend.sigiv.Backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sigiv.Backend.sigiv.Backend.entity.Devolucion;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    List<Devolucion> findByVentaIdventa(Long ventaId);

    @Query("SELECT d FROM Devolucion d WHERE d.venta.usuario.empresa.idEmpresa = :empresaId")
    List<Devolucion> findByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT d FROM Devolucion d WHERE d.venta.usuario.empresa.idEmpresa = :empresaId")
    Page<Devolucion> findByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT d FROM Devolucion d WHERE d.venta.idventa = :ventaId AND d.venta.usuario.empresa.idEmpresa = :empresaId")
    List<Devolucion> findByVentaIdAndEmpresaId(@Param("ventaId") Long ventaId, @Param("empresaId") Long empresaId);

    @Query("SELECT d FROM Devolucion d WHERE d.venta.usuario.empresa.idEmpresa = :empresaId AND d.venta.usuario.idUsuario = :usuarioId")
    Page<Devolucion> findByEmpresaIdAndUsuarioId(
            @Param("empresaId") Long empresaId,
            @Param("usuarioId") Long usuarioId,
            Pageable pageable);

    @Query("SELECT d FROM Devolucion d WHERE d.venta.idventa = :ventaId AND d.venta.usuario.empresa.idEmpresa = :empresaId AND d.venta.usuario.idUsuario = :usuarioId")
    List<Devolucion> findByVentaIdAndEmpresaIdAndUsuarioId(
            @Param("ventaId") Long ventaId,
            @Param("empresaId") Long empresaId,
            @Param("usuarioId") Long usuarioId);
}

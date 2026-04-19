package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;

public interface CotizacionService {
    CotizacionResponseDto crearCotizacion(CotizacionRequestDto dto);
    List<CotizacionResponseDto> listarCotizaciones();
    CotizacionResponseDto obtenerCotizacion(Long id);
    void eliminarCotizacion(Long id);
    List<DetalleCotizacion> findByCotizacionId(Long idcotizacion);
    
    // Nuevo método para listar cotizaciones con paginado
    Page<CotizacionResponseDto> listarCotizacionesPorEmpresa(
            Long empresaId,
            int page,
            int size,
            Long usuarioId,
            String nombreCliente,
            String fechaInicio,
            String fechaFin
    );
    
    // Método para generar cotización en PDF
    byte[] generarCotizacionPdf(Long id);
}

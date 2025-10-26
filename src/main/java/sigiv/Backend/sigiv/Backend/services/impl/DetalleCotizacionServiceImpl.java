package sigiv.Backend.sigiv.Backend.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Cotizacion;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.repository.CotizacionRepository;
import sigiv.Backend.sigiv.Backend.repository.DetalleCotizacionRepository;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.services.DetalleCotizacionService;

@Service
public class DetalleCotizacionServiceImpl implements DetalleCotizacionService {

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private ProductoRepository productoRepository;
    
    @Override
public List<DetalleCotizacion> findByCotizacionId(Long idcotizacion) {
    return detalleCotizacionRepository.findByCotizacion_Idcotizacion(idcotizacion);
}

    @Override
    public DetalleCotizacionResponseDto agregarDetalleACotizacion(Long cotizacionId, DetalleCotizacionRequestDto dto) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetalleCotizacion detalle = new DetalleCotizacion();
        detalle.setCotizacion(cotizacion);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecio(producto.getPrecio());
        detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(dto.getCantidad())));
        detalle.setDescripcionProducto(producto.getNombre());

        detalleCotizacionRepository.save(detalle);

        return new DetalleCotizacionResponseDto(
                producto.getNombre(),
                detalle.getCantidad(),
                detalle.getPrecio(),
                detalle.getSubtotal()
        );
    }

    @Override
    public void eliminarDetalle(Long detalleId) {
        detalleCotizacionRepository.deleteById(detalleId);
    }

    @Override
    public void eliminarTodosLosDetallesDeCotizacion(Long cotizacionId) {
        List<DetalleCotizacion> detalles = detalleCotizacionRepository.findByCotizacion_Idcotizacion(cotizacionId);
        detalleCotizacionRepository.deleteAll(detalles);
    }
}


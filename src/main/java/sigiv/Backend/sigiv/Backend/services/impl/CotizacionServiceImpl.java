package sigiv.Backend.sigiv.Backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.cotizacion.CotizacionResponseDto;
import sigiv.Backend.sigiv.Backend.dto.detalleCotizacion.DetalleCotizacionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.mapper.CotizacionMapper;
import sigiv.Backend.sigiv.Backend.dto.mapper.DetalleCotizacionMapper;
import sigiv.Backend.sigiv.Backend.entity.*;
import sigiv.Backend.sigiv.Backend.repository.*;
import sigiv.Backend.sigiv.Backend.services.CotizacionService;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    @Override
public List<DetalleCotizacion> findByCotizacionId(Long idcotizacion) {
    return detalleCotizacionRepository.findByCotizacion_Idcotizacion(idcotizacion);
}
    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Autowired
    private CotizacionMapper cotizacionMapper;

    @Autowired
    private DetalleCotizacionMapper detalleMapper;

    @Override
    @Transactional
 
public CotizacionResponseDto crearCotizacion(CotizacionRequestDto dto) {
    if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
        throw new RuntimeException("La cotización debe tener al menos un detalle.");
    }

    Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    Cotizacion cot = cotizacionMapper.toEntity(dto, usuario);
    cot.setFecha(LocalDateTime.now());
    cot.setTotal(BigDecimal.ZERO);
    cotizacionRepository.save(cot);

    BigDecimal total = BigDecimal.ZERO;

    // 🔹 Creamos una lista para mantener los detalles en memoria
    List<DetalleCotizacion> listaDetalles = new java.util.ArrayList<>();

    for (DetalleCotizacionRequestDto detalleDto : dto.getDetalles()) {
        Producto producto = productoRepository.findById(detalleDto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        BigDecimal subtotal = producto.getPrecio()
                .multiply(BigDecimal.valueOf(detalleDto.getCantidad()));

        DetalleCotizacion detalle = detalleMapper.toEntity(detalleDto, cot, producto, subtotal);
        detalleCotizacionRepository.save(detalle);

        total = total.add(subtotal);

        // ✅ Agregamos a la lista en memoria
        listaDetalles.add(detalle);
    }

    cot.setTotal(total);
    cot.setDetalles(listaDetalles); // ✅ Asociamos los detalles en memoria
    cotizacionRepository.save(cot);

    // ✅ Retornamos con los detalles ya cargados
    return cotizacionMapper.toDto(cot);
}

    @Override
    public List<CotizacionResponseDto> listarCotizaciones() {
        return cotizacionRepository.findAll()
                .stream()
                .map(cotizacionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CotizacionResponseDto obtenerCotizacion(Long id) {
        Cotizacion cot = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        return cotizacionMapper.toDto(cot);
    }

    @Override
    public void eliminarCotizacion(Long id) {
        cotizacionRepository.deleteById(id);
    }
}

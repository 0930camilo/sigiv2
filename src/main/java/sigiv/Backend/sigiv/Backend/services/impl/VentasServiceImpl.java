package sigiv.Backend.sigiv.Backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.mapper.DetalleVentaMapper;
import sigiv.Backend.sigiv.Backend.dto.mapper.VentasMapper;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.entity.DetalleVentas;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.entity.Ventas;
import sigiv.Backend.sigiv.Backend.repository.DetalleVentaRepository;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.repository.UsuarioRepository;
import sigiv.Backend.sigiv.Backend.repository.VentasRepository;
import sigiv.Backend.sigiv.Backend.services.VentasService;

@Service
public class VentasServiceImpl implements VentasService {

    @Autowired
    private VentasRepository ventasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VentasMapper ventasMapper;

    @Autowired
    private DetalleVentaMapper detalleVentaMapper;

    @Override
    @Transactional
    public VentasResponseDto crearVenta(VentasRequestDto dto) {

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear venta
        Ventas venta = ventasMapper.toEntity(dto, usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(BigDecimal.ZERO); // Se calculará automáticamente
        ventasRepository.save(venta);

        BigDecimal totalVenta = BigDecimal.ZERO;

        // Procesar detalles
        for (DetalleVentaRequestDto detalleDto : dto.getDetalles()) {

            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar cantidad disponible
            if (detalleDto.getCantidad() > producto.getCantidad()) {
                throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getNombre());
            }

            // Calcular subtotal
            BigDecimal subtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(detalleDto.getCantidad()));

            // Crear detalle de venta
            DetalleVentas detalle = detalleVentaMapper.toEntity(detalleDto, venta, producto, subtotal);
            detalleVentaRepository.save(detalle);

            // Actualizar stock del producto
            producto.setCantidad(producto.getCantidad() - detalleDto.getCantidad());
            productoRepository.save(producto);

            totalVenta = totalVenta.add(subtotal);
        }

        // Actualizar total de la venta
        venta.setTotal(totalVenta);
        ventasRepository.save(venta);

        return ventasMapper.toDto(venta);
    }

    @Override
    public List<VentasResponseDto> listarVentas() {
        return ventasRepository.findAll()
                .stream()
                .map(ventasMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VentasResponseDto obtenerVenta(Long id) {
        Ventas venta = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return ventasMapper.toDto(venta);
    }

    @Override
    public void eliminarVenta(Long id) {
        if (!ventasRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }
        ventasRepository.deleteById(id);
    }
}

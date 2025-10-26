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

        Ventas venta = ventasMapper.toEntity(dto, usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(BigDecimal.ZERO);
        ventasRepository.save(venta);

        BigDecimal totalVenta = BigDecimal.ZERO;

        for (DetalleVentaRequestDto detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (detalleDto.getCantidad() > producto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleDto.getCantidad()));

            DetalleVentas detalle = detalleVentaMapper.toEntity(detalleDto, venta, producto, subtotal);
            detalleVentaRepository.save(detalle);

            producto.setCantidad(producto.getCantidad() - detalleDto.getCantidad());
            productoRepository.save(producto);

            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);
        if (dto.getEfectivo() != null) {
            venta.setCambio(dto.getEfectivo().subtract(totalVenta));
        }

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
    @Transactional
    public VentasResponseDto editarVenta(Long id, VentasRequestDto dto) {
        Ventas venta = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        // Actualizar datos básicos del cliente o efectivo
        venta.setNombreCliente(dto.getNombreCliente());
        venta.setTelefonoCliente(dto.getTelefonoCliente());
        venta.setEfectivo(dto.getEfectivo());

        // Eliminar detalles antiguos y reponer stock
        List<DetalleVentas> detallesAntiguos = detalleVentaRepository.findByVentaIdventa(id);
        for (DetalleVentas d : detallesAntiguos) {
            Producto producto = d.getProducto();
            if (producto != null) {
                producto.setCantidad(producto.getCantidad() + d.getCantidad());
                productoRepository.save(producto);
            }
        }
        detalleVentaRepository.deleteAll(detallesAntiguos);

        // Agregar nuevos detalles
        BigDecimal totalVenta = BigDecimal.ZERO;
        for (DetalleVentaRequestDto detalleDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (detalleDto.getCantidad() > producto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleDto.getCantidad()));

            DetalleVentas nuevoDetalle = detalleVentaMapper.toEntity(detalleDto, venta, producto, subtotal);
            detalleVentaRepository.save(nuevoDetalle);

            producto.setCantidad(producto.getCantidad() - detalleDto.getCantidad());
            productoRepository.save(producto);

            totalVenta = totalVenta.add(subtotal);
        }

        venta.setTotal(totalVenta);
        if (dto.getEfectivo() != null) {
            venta.setCambio(dto.getEfectivo().subtract(totalVenta));
        }

        ventasRepository.save(venta);
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

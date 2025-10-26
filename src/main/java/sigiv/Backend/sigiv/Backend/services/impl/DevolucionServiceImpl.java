package sigiv.Backend.sigiv.Backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionRequestDto;
import sigiv.Backend.sigiv.Backend.dto.devol.DevolucionResponseDto;
import sigiv.Backend.sigiv.Backend.entity.*;
import sigiv.Backend.sigiv.Backend.repository.*;
import sigiv.Backend.sigiv.Backend.services.DevolucionService;

@Service
@RequiredArgsConstructor
public class DevolucionServiceImpl implements DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final VentasRepository ventasRepository;

    @Override
    @Transactional
    public DevolucionResponseDto registrarDevolucion(DevolucionRequestDto dto) {

        // 1️⃣ Buscar venta y producto
        Ventas venta = ventasRepository.findById(dto.getVentaId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + dto.getVentaId()));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId()));

        // 2️⃣ Buscar detalle de venta
        DetalleVentas detalle = detalleVentaRepository.findByVentaIdventa(venta.getIdventa()).stream()
                .filter(d -> d.getProducto().getIdproducto().equals(producto.getIdproducto()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El producto no pertenece a esta venta."));

        // 3️⃣ Validar cantidad
        if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad devuelta debe ser mayor que 0");
        }

        if (dto.getCantidad() > detalle.getCantidad()) {
            throw new IllegalArgumentException("No puedes devolver más de lo vendido");
        }

        // 4️⃣ Reintegrar al inventario
        producto.setCantidad(producto.getCantidad() + dto.getCantidad());
        productoRepository.save(producto);

        // 5️⃣ Actualizar detalle de venta
        detalle.setCantidad(detalle.getCantidad() - dto.getCantidad());
        detalle.setSubtotal(detalle.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));
        detalleVentaRepository.save(detalle);

        // 6️⃣ Recalcular total de la venta
        BigDecimal nuevoTotal = detalleVentaRepository.findByVentaIdventa(venta.getIdventa()).stream()
                .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        venta.setTotal(nuevoTotal);
        if (venta.getEfectivo() != null) {
            venta.setCambio(venta.getEfectivo().subtract(nuevoTotal));
        }
        ventasRepository.save(venta);

        // 7️⃣ Registrar devolución
        Devolucion devolucion = Devolucion.builder()
                .venta(venta)
                .producto(producto)
                .cantidad(dto.getCantidad())
                .motivo(dto.getMotivo())
                .fecha(LocalDateTime.now())
                .build();

        devolucionRepository.save(devolucion);

        // 8️⃣ Retornar DTO correctamente
        DevolucionResponseDto respuesta = new DevolucionResponseDto();
        respuesta.setIddevolucion(devolucion.getIdDevolucion());
        respuesta.setVentaId(venta.getIdventa());
        respuesta.setProductoId(producto.getIdproducto());
        respuesta.setNombreProducto(producto.getNombre());
        respuesta.setCantidad(devolucion.getCantidad());
        respuesta.setMotivo(devolucion.getMotivo());
        respuesta.setFecha(devolucion.getFecha());

        return respuesta;
    }

    @Override
    public List<DevolucionResponseDto> listarDevolucionesPorVenta(Long ventaId) {
        return devolucionRepository.findByVentaIdventa(ventaId).stream()
                .map(dev -> {
                    DevolucionResponseDto dto = new DevolucionResponseDto();
                    dto.setIddevolucion(dev.getIdDevolucion());
                    dto.setVentaId(dev.getVenta().getIdventa());
                    dto.setProductoId(dev.getProducto().getIdproducto());
                    dto.setNombreProducto(dev.getProducto().getNombre());
                    dto.setCantidad(dev.getCantidad());
                    dto.setMotivo(dev.getMotivo());
                    dto.setFecha(dev.getFecha());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

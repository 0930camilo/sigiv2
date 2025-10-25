package sigiv.Backend.sigiv.Backend.services.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.mapper.DetalleVentaMapper;
import sigiv.Backend.sigiv.Backend.entity.DetalleVentas;
import sigiv.Backend.sigiv.Backend.entity.Producto;
import sigiv.Backend.sigiv.Backend.entity.Ventas;
import sigiv.Backend.sigiv.Backend.repository.DetalleVentaRepository;
import sigiv.Backend.sigiv.Backend.repository.ProductoRepository;
import sigiv.Backend.sigiv.Backend.repository.VentasRepository;
import sigiv.Backend.sigiv.Backend.services.DetalleVentasService;

@Service
@RequiredArgsConstructor
public class DetalleVentasServiceImpl implements DetalleVentasService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final VentasRepository ventasRepository;
    private final DetalleVentaMapper detalleVentaMapper;

    @Override
    @Transactional
    public DetalleVentaResponseDto agregarDetalleAVenta(Long ventaId, DetalleVentaRequestDto dto) {
        // 1️⃣ Buscar la venta
        Ventas venta = ventasRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + ventaId));

        // 2️⃣ Buscar el producto
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId()));

        // 3️⃣ Validar cantidad
        if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
        if (producto.getCantidad() < dto.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        // 4️⃣ Calcular subtotal
        BigDecimal precio = producto.getPrecio() != null ? producto.getPrecio() : BigDecimal.ZERO;
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(dto.getCantidad()));

        // 5️⃣ Crear y guardar detalle
        DetalleVentas detalle = detalleVentaMapper.toEntity(dto, venta, producto, subtotal);
        detalleVentaRepository.save(detalle);

        // 6️⃣ Descontar stock
        producto.setCantidad(producto.getCantidad() - dto.getCantidad());
        productoRepository.save(producto);

        // 7️⃣ Recalcular total de venta
        BigDecimal nuevoTotal = detalleVentaRepository.findByVentaIdventa(ventaId).stream()
                .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        venta.setTotal(nuevoTotal);
        if (venta.getEfectivo() != null) {
            venta.setCambio(venta.getEfectivo().subtract(nuevoTotal));
        }
        ventasRepository.save(venta);

        // 8️⃣ Retornar respuesta
        return detalleVentaMapper.toDto(detalle);
    }

    @Override
    @Transactional
    public void eliminarDetalle(Long detalleId) {
        // Buscar detalle
        DetalleVentas detalle = detalleVentaRepository.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado con ID: " + detalleId));

        Producto producto = detalle.getProducto();
        Ventas venta = detalle.getVenta();

        // Reponer stock
        if (producto != null && detalle.getCantidad() != null) {
            producto.setCantidad(producto.getCantidad() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        // Eliminar detalle
        detalleVentaRepository.delete(detalle);

        // Recalcular total
        if (venta != null && venta.getIdventa() != null) {
            BigDecimal nuevoTotal = detalleVentaRepository.findByVentaIdventa(venta.getIdventa()).stream()
                    .map(d -> d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            venta.setTotal(nuevoTotal);
            if (venta.getEfectivo() != null) {
                venta.setCambio(venta.getEfectivo().subtract(nuevoTotal));
            }
            ventasRepository.save(venta);
        }
    }
    @Override
@Transactional
public void eliminarTodosLosDetallesDeVenta(Long ventaId) {
    // Buscar venta
    Ventas venta = ventasRepository.findById(ventaId)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + ventaId));

    // Obtener todos los detalles asociados a la venta
    var detalles = detalleVentaRepository.findByVentaIdventa(ventaId);

    // Reponer stock de cada producto antes de eliminar
    for (DetalleVentas detalle : detalles) {
        Producto producto = detalle.getProducto();
        if (producto != null && detalle.getCantidad() != null) {
            producto.setCantidad(producto.getCantidad() + detalle.getCantidad());
            productoRepository.save(producto);
        }
    }

    // Eliminar todos los detalles
    detalleVentaRepository.deleteAll(detalles);

    // Reiniciar total y cambio de la venta
    venta.setTotal(BigDecimal.ZERO);
    if (venta.getEfectivo() != null) {
        venta.setCambio(venta.getEfectivo()); // el cambio es igual al efectivo si ya no hay productos
    }
    ventasRepository.save(venta);
}

}

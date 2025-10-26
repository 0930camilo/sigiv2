package sigiv.Backend.sigiv.Backend.services;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;

public interface DetalleVentasService {

    /**
     * Agrega un detalle a una venta específica.
     * Valida el stock, descuenta cantidad del producto y recalcula el total de la venta.
     */
    DetalleVentaResponseDto agregarDetalleAVenta(Long ventaId, DetalleVentaRequestDto dto);

    /**
     * Elimina un detalle de venta.
     * Repone el stock del producto y actualiza el total de la venta.
     */
    void eliminarDetalle(Long detalleId);
    void eliminarTodosLosDetallesDeVenta(Long ventaId);
}

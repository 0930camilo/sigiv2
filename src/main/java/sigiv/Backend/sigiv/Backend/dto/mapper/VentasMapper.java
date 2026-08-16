package sigiv.Backend.sigiv.Backend.dto.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sigiv.Backend.sigiv.Backend.dto.detalleVenta.DetalleVentaResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.entity.Ventas;

@Component
public class VentasMapper {

    @Autowired
    private DetalleVentaMapper detalleMapper;

    public Ventas toEntity(VentasRequestDto dto, Usuario usuario, Empresa empresa) {
        Ventas venta = new Ventas();
        venta.setUsuario(usuario);
        venta.setEmpresa(empresa);
        
        String nombre = (dto.getNombreCliente() == null || dto.getNombreCliente().isBlank()) 
                        ? "NN" : dto.getNombreCliente();
        venta.setNombreCliente(nombre);
        
        venta.setTelefonoCliente(dto.getTelefonoCliente());
        venta.setCorreoCliente(dto.getCorreoCliente());
        
        String documento = (dto.getDocumentoCliente() == null || dto.getDocumentoCliente().isBlank()) 
                           ? "999999999" : dto.getDocumentoCliente();
        venta.setDocumentoCliente(documento);
        
        venta.setEfectivo(dto.getEfectivo());
        venta.setTipoPago(dto.getTipoPago()); // Asignar tipo de pago
        return venta;
    }

    public VentasResponseDto toDto(Ventas entity) {
        BigDecimal totalAbonado = entity.getAbonos() != null
                ? entity.getAbonos().stream()
                .map(abono -> abono.getValor() != null ? abono.getValor() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;
        BigDecimal totalSeguro = entity.getTotal() != null ? entity.getTotal() : BigDecimal.ZERO;
        BigDecimal saldoPendiente = totalSeguro.subtract(totalAbonado);

        List<DetalleVentaResponseDto> detalles = entity.getDetalles() != null
                ? entity.getDetalles().stream()
                        .map(detalleMapper::toDto)
                        .collect(Collectors.toList())
                : null;

        return new VentasResponseDto(
                entity.getIdventa(),
                entity.getFecha(),
                entity.getNombreCliente(),
                entity.getTelefonoCliente(),
                entity.getCorreoCliente(),
                entity.getDocumentoCliente(),
                entity.getSubtotal(),
                entity.getDescuentoTotal(),
                entity.getTotal(),
                totalAbonado,
                saldoPendiente,
                entity.getEfectivo(),
                entity.getCambio(),
                entity.getUsuario() != null ? entity.getUsuario().getNombres() : null,
                entity.getEmpresa() != null ? entity.getEmpresa().getIdEmpresa() : null, // Asignar empresaId
                entity.getTipoPago(),
                entity.getEstadoPago(),
                detalles
        );
    }
}
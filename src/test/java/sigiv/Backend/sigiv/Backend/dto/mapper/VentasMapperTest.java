package sigiv.Backend.sigiv.Backend.dto.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import sigiv.Backend.sigiv.Backend.dto.ventas.VentasResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Abono;
import sigiv.Backend.sigiv.Backend.entity.Ventas;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VentasMapperTest {

    private final VentasMapper mapper = new VentasMapper();

    @Test
    void debeMapearTotalAbonadoYSaldoPendienteDesdeAbonos() {
        Ventas venta = new Ventas();
        venta.setIdventa(1L);
        venta.setNombreCliente("Cliente");
        venta.setTotal(new BigDecimal("30000"));

        Abono abono1 = new Abono();
        abono1.setValor(new BigDecimal("7000"));
        Abono abono2 = new Abono();
        abono2.setValor(new BigDecimal("3000"));
        venta.setAbonos(List.of(abono1, abono2));

        VentasResponseDto dto = mapper.toDto(venta);

        assertEquals(new BigDecimal("10000"), dto.getTotalAbonado());
        assertEquals(new BigDecimal("20000"), dto.getSaldoPendiente());
    }
}


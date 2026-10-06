package sigiv.Backend.sigiv.Backend.services.impl;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import sigiv.Backend.sigiv.Backend.dto.ventas.VentasRequestDto;
import sigiv.Backend.sigiv.Backend.entity.Abono;
import sigiv.Backend.sigiv.Backend.entity.Ventas.TipoPago;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VentasServiceImplTipoPagoTest {

    private final VentasServiceImpl ventasService = new VentasServiceImpl();

    @Test
    void debeResolverCreditoCuandoLlegaAbonoInicialSinTipoPago() {
        VentasRequestDto dto = new VentasRequestDto();
        dto.setAbonoInicial(new BigDecimal("10000"));

        TipoPago tipoPago = ventasService.resolverTipoPago(dto);

        assertEquals(TipoPago.CREDITO, tipoPago);
    }

    @Test
    void debeResolverCreditoCuandoLlegaMetodoAbonoSinTipoPago() {
        VentasRequestDto dto = new VentasRequestDto();
        dto.setMetodoPagoAbonoInicial(Abono.MetodoPago.EFECTIVO);

        TipoPago tipoPago = ventasService.resolverTipoPago(dto);

        assertEquals(TipoPago.CREDITO, tipoPago);
    }

    @Test
    void debeResolverContadoCuandoNoLlegaInformacionDeCredito() {
        VentasRequestDto dto = new VentasRequestDto();

        TipoPago tipoPago = ventasService.resolverTipoPago(dto);

        assertEquals(TipoPago.CONTADO, tipoPago);
    }

    @Test
    void debeFallarSiEsContadoConAbonoInicialMayorACero() {
        VentasRequestDto dto = new VentasRequestDto();
        dto.setTipoPago(TipoPago.CONTADO);
        dto.setAbonoInicial(new BigDecimal("1"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ventasService.resolverTipoPago(dto)
        );

        assertEquals("No se puede registrar abono inicial cuando el tipo de pago es CONTADO.", ex.getMessage());
    }
}


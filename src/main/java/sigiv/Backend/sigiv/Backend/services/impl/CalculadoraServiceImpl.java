package sigiv.Backend.sigiv.Backend.services.impl;

import org.springframework.stereotype.Service;
import sigiv.Backend.sigiv.Backend.dto.calculadora.CalculadoraResponseDto;
import sigiv.Backend.sigiv.Backend.dto.calculadora.PorcentajeRequestDto;
import sigiv.Backend.sigiv.Backend.dto.calculadora.ReglaDeTresRequestDto;
import sigiv.Backend.sigiv.Backend.services.CalculadoraService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculadoraServiceImpl implements CalculadoraService {

    @Override
    public CalculadoraResponseDto calcularPorcentaje(PorcentajeRequestDto dto) {
        if (dto.getValor() == null || dto.getPorcentaje() == null) {
            throw new IllegalArgumentException("El valor y el porcentaje son obligatorios");
        }
        
        // resultado = (valor * porcentaje) / 100
        BigDecimal resultado = dto.getValor()
                .multiply(dto.getPorcentaje())
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        
        return new CalculadoraResponseDto(resultado, "PORCENTAJE");
    }

    @Override
    public CalculadoraResponseDto calcularReglaDeTres(ReglaDeTresRequestDto dto) {
        if (dto.getA() == null || dto.getB() == null || dto.getC() == null) {
            throw new IllegalArgumentException("Los valores A, B y C son obligatorios");
        }
        
        if (dto.getA().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("El valor A no puede ser cero en una regla de tres (división por cero)");
        }
        
        // Regla de tres simple directa:
        // A -> B
        // C -> X
        // X = (B * C) / A
        
        BigDecimal resultado = dto.getB()
                .multiply(dto.getC())
                .divide(dto.getA(), 4, RoundingMode.HALF_UP);
        
        return new CalculadoraResponseDto(resultado, "REGLA_DE_TRES");
    }
}

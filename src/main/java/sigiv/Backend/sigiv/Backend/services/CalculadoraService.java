package sigiv.Backend.sigiv.Backend.services;

import sigiv.Backend.sigiv.Backend.dto.calculadora.CalculadoraResponseDto;
import sigiv.Backend.sigiv.Backend.dto.calculadora.PorcentajeRequestDto;
import sigiv.Backend.sigiv.Backend.dto.calculadora.ReglaDeTresRequestDto;

public interface CalculadoraService {
    CalculadoraResponseDto calcularPorcentaje(PorcentajeRequestDto dto);
    CalculadoraResponseDto calcularReglaDeTres(ReglaDeTresRequestDto dto);
}

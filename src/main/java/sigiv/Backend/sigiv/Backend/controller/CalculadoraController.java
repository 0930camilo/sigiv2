package sigiv.Backend.sigiv.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigiv.Backend.sigiv.Backend.dto.calculadora.CalculadoraResponseDto;
import sigiv.Backend.sigiv.Backend.dto.calculadora.PorcentajeRequestDto;
import sigiv.Backend.sigiv.Backend.dto.calculadora.ReglaDeTresRequestDto;
import sigiv.Backend.sigiv.Backend.services.CalculadoraService;

@RestController
@RequestMapping("/api/calculadora")
@CrossOrigin(origins = "*")
public class CalculadoraController {

    @Autowired
    private CalculadoraService calculadoraService;

    @PostMapping("/porcentaje")
    public ResponseEntity<CalculadoraResponseDto> calcularPorcentaje(@RequestBody PorcentajeRequestDto dto) {
        return ResponseEntity.ok(calculadoraService.calcularPorcentaje(dto));
    }

    @PostMapping("/regla-de-tres")
    public ResponseEntity<CalculadoraResponseDto> calcularReglaDeTres(@RequestBody ReglaDeTresRequestDto dto) {
        return ResponseEntity.ok(calculadoraService.calcularReglaDeTres(dto));
    }
}

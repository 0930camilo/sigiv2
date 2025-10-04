package sigiv.Backend.sigiv.Backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.Nomina;
import sigiv.Backend.sigiv.Backend.repository.NominaRepository;
import sigiv.Backend.sigiv.Backend.repository.PersonaNominaRepository;

@RestController
@RequestMapping("/nominas")
public class NominaController {

    @Autowired
    private NominaRepository nominaRepository;
    @Autowired
    private PersonaNominaRepository personaNominaRepository;

    // Obtener todas las nóminas
    @GetMapping
    public ResponseEntity<List<Nomina>> obtenerTodas() {
        return ResponseEntity.ok(nominaRepository.findAll());
    }

    // Obtener nómina por ID
    @GetMapping("/{id}")
    public ResponseEntity<Nomina> obtenerPorId(@PathVariable Long id) {
        return nominaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
// Guardar nueva nómina
@PostMapping
public ResponseEntity<Nomina> guardar(@RequestBody Nomina nuevaNomina) {
    Nomina guardada = nominaRepository.save(nuevaNomina);

    // 🔹 calcular total (aunque será 0 si no hay personas asociadas todavía)
    BigDecimal total = personaNominaRepository.calcularTotalPorNomina(guardada.getIdnomina());
    guardada.setTotalPago(total);

    Nomina actualizada = nominaRepository.save(guardada);

    return ResponseEntity.ok(actualizada);
}

    @PutMapping("/{id}")
    public ResponseEntity<Nomina> actualizar(@PathVariable Long id, @RequestBody Nomina nominaActualizada) {
        return nominaRepository.findById(id)
                .map(nomina -> {
                    nomina.setDescripcion(nominaActualizada.getDescripcion());
                    nomina.setFechaInicio(nominaActualizada.getFechaInicio());
                    nomina.setFechaFin(nominaActualizada.getFechaFin());
                    nomina.setEmpresa(nominaActualizada.getEmpresa());

                    Nomina actualizada = nominaRepository.save(nomina);

                    // recalcular total al actualizar
                    BigDecimal total = personaNominaRepository.calcularTotalPorNomina(actualizada.getIdnomina());
                    actualizada.setTotalPago(total);
                    nominaRepository.save(actualizada);

                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // Eliminar nómina (física)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (nominaRepository.existsById(id)) {
            nominaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}

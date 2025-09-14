package sigiv.Backend.sigiv.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.PersonaNomina;
import sigiv.Backend.sigiv.Backend.entity.PersonaNominaId;
import sigiv.Backend.sigiv.Backend.repository.PersonaNominaRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/persona-nomina")
public class PersonaNominaController {

    @Autowired
    private PersonaNominaRepository personaNominaRepository;

    // 🔹 Listar todas las relaciones persona-nómina
    @GetMapping
    public ResponseEntity<List<PersonaNomina>> listarTodos() {
        return ResponseEntity.ok(personaNominaRepository.findAll());
    }

    // 🔹 Buscar por idpersona e idnomina (clave compuesta)
    @GetMapping("/{idpersona}/{idnomina}")
    public ResponseEntity<PersonaNomina> obtenerPorId(
            @PathVariable Long idpersona,
            @PathVariable Long idnomina) {

        PersonaNominaId id = new PersonaNominaId();
        id.setIdpersona(idpersona);
        id.setIdnomina(idnomina);
        Optional<PersonaNomina> personaNomina = personaNominaRepository.findById(id);

        return personaNomina.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Actualizar días trabajados o valor día
    @PutMapping("/{idpersona}/{idnomina}")
    public ResponseEntity<PersonaNomina> actualizar(
            @PathVariable Long idpersona,
            @PathVariable Long idnomina,
            @RequestBody PersonaNomina datosActualizados) {

        PersonaNominaId id = new PersonaNominaId();
        id.setIdpersona(idpersona);
        id.setIdnomina(idnomina);

        return personaNominaRepository.findById(id).map(personaNomina -> {
            personaNomina.setDiasTrabajados(datosActualizados.getDiasTrabajados());
            personaNomina.setValorDia(datosActualizados.getValorDia());
            PersonaNomina actualizado = personaNominaRepository.save(personaNomina);
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Eliminar relación persona-nómina
    @DeleteMapping("/{idpersona}/{idnomina}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long idpersona,
            @PathVariable Long idnomina) {

        PersonaNominaId id = new PersonaNominaId();
        id.setIdpersona(idpersona);
        id.setIdnomina(idnomina);
        if (personaNominaRepository.existsById(id)) {
            personaNominaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

package sigiv.Backend.sigiv.Backend.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sigiv.Backend.sigiv.Backend.entity.Persona;
import sigiv.Backend.sigiv.Backend.repository.PersonaRepository;

@RestController
@RequestMapping("/persona")
public class PersonaController {

    @Autowired
    private PersonaRepository personaRepository;

    // Mostrar todas las personas
    @GetMapping("/mostrar")
    public List<Persona> obtenerTodas() {
        return personaRepository.findAll();
    }

    // Personas activas
    @GetMapping("/activas")
    public List<Persona> obtenerActivas() {
        return personaRepository.findByEstado(Persona.Estado.Activo);
    }

    // Personas inactivas
    @GetMapping("/inactivas")
    public List<Persona> obtenerInactivas() {
        return personaRepository.findByEstado(Persona.Estado.Inactivo);
    }

    // Buscar persona por ID
    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPorId(@PathVariable Long id) {
        return personaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Guardar nueva persona
    @PostMapping("/guardar")
    public Persona guardar(@RequestBody Persona nuevaPersona) {
        return personaRepository.save(nuevaPersona);
    }

    // Actualizar persona existente
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Persona> actualizar(@PathVariable Long id, @RequestBody Persona personaActualizada) {
        return personaRepository.findById(id)
                .map(persona -> {
                    persona.setNombre(personaActualizada.getNombre());
                    persona.setCorreo(personaActualizada.getCorreo());
                    persona.setTelefono(personaActualizada.getTelefono());
                    persona.setDireccion(personaActualizada.getDireccion());
                    persona.setFechaNacimiento(personaActualizada.getFechaNacimiento());
                    persona.setFechaIngreso(personaActualizada.getFechaIngreso());
                    persona.setEstado(personaActualizada.getEstado());
                    persona.setEmpresa(personaActualizada.getEmpresa());
                    Persona actualizada = personaRepository.save(persona);
                    return ResponseEntity.ok(actualizada);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar persona físicamente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (personaRepository.existsById(id)) {
            personaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminación lógica (cambiar estado a Inactivo)
    @PutMapping("/eliminar/{id}")
    public ResponseEntity<Persona> eliminarLogicamente(@PathVariable Long id) {
        return personaRepository.findById(id)
                .map(persona -> {
                    persona.setEstado(Persona.Estado.Inactivo);
                    Persona actualizada = personaRepository.save(persona);
                    return ResponseEntity.ok(actualizada);
                }).orElse(ResponseEntity.notFound().build());
    }

  
}

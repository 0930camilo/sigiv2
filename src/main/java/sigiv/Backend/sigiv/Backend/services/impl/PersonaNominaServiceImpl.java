package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.PersonaNominaMapper;
import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.PersonaNomina;
import sigiv.Backend.sigiv.Backend.entity.Persona;
import sigiv.Backend.sigiv.Backend.entity.Nomina;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.PersonaNominaRepository;
import sigiv.Backend.sigiv.Backend.repository.PersonaRepository;
import sigiv.Backend.sigiv.Backend.repository.NominaRepository;
import sigiv.Backend.sigiv.Backend.services.PersonaNominaService;

@Service
@RequiredArgsConstructor
public class PersonaNominaServiceImpl implements PersonaNominaService {

    private final PersonaNominaRepository personaNominaRepository;
    private final PersonaRepository personaRepository;
    private final NominaRepository nominaRepository;

    // ✅ Crear una nueva relación PersonaNomina
    @Override
    public PersonaNominaResponseDto crearPersonaNomina(PersonaNominaRequestDto dto) {
        // Buscar persona y nómina
        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", dto.getIdPersona()));

        Nomina nomina = nominaRepository.findById(dto.getIdNomina())
                .orElseThrow(() -> new ResourceNotFoundException("Nomina", "id", dto.getIdNomina()));

        // Mapear DTO → Entidad
        PersonaNomina personaNomina = PersonaNominaMapper.toEntityForCreate(dto, persona, nomina);

        // Guardar en la base de datos
        PersonaNomina guardada = personaNominaRepository.save(personaNomina);

        // Retornar DTO de respuesta
        return PersonaNominaMapper.toDto(guardada);
    }

    // ✅ Obtener una PersonaNomina por ID compuesto (idPersona + idNomina)
    @Override
    public PersonaNominaResponseDto obtenerPorId(Long idPersona) {
        PersonaNomina personaNomina = personaNominaRepository.findAll().stream()
                .filter(pn -> pn.getIdpersona().equals(idPersona))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PersonaNomina", "idPersona", idPersona));

        return PersonaNominaMapper.toDto(personaNomina);
    }

    // ✅ Actualizar una PersonaNomina existente
    @Override
    public PersonaNominaResponseDto actualizarPersonaNomina(Long idPersona, PersonaNominaRequestDto dto) {
        PersonaNomina personaNomina = personaNominaRepository.findAll().stream()
                .filter(pn -> pn.getIdpersona().equals(idPersona))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PersonaNomina", "idPersona", idPersona));

        PersonaNominaMapper.updateEntityFromDto(dto, personaNomina);

        PersonaNomina actualizada = personaNominaRepository.save(personaNomina);
        return PersonaNominaMapper.toDto(actualizada);
    }

    // ✅ Listar todas las relaciones PersonaNomina
    @Override
    public List<PersonaNominaResponseDto> listarPersonas() {
        return personaNominaRepository.findAll()
                .stream()
                .map(PersonaNominaMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Eliminar una relación PersonaNomina (por idPersona)
    @Override
    public void eliminarPersona(Long idPersona) {
        PersonaNomina personaNomina = personaNominaRepository.findAll().stream()
                .filter(pn -> pn.getIdpersona().equals(idPersona))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("PersonaNomina", "idPersona", idPersona));

        personaNominaRepository.delete(personaNomina);
    }

    // ✅ (Opcional) cambiar estado si tu entidad tuviera un campo de estado
    @Override
    public PersonaNominaResponseDto cambiarEstado(Long id) {
        throw new UnsupportedOperationException("Este método no aplica a PersonaNomina, no tiene campo 'estado'.");
    }
}

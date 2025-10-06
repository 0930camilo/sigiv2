package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.mapper.PersonaMapper;

import sigiv.Backend.sigiv.Backend.dto.persona.PersonaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Persona;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.PersonaRepository;
import sigiv.Backend.sigiv.Backend.services.PersonaService;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository personaRepository;
    private final EmpresaRepository empresaRepository;

    @Override
    public PersonaResponseDto crearPersona(PersonaRequestDto dto) {
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("empresa no encontrada"));
        }
        Persona persona = PersonaMapper.toEntityForCreate(dto, empresa);
        Persona guardada = personaRepository.save(persona);
        return PersonaMapper.toDto(guardada);
    }

    @Override
    public PersonaResponseDto obtenerPorId(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));
        return PersonaMapper.toDto(persona);
    }

    @Override
    public PersonaResponseDto actualizarPersona(Long id, PersonaRequestDto dto) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));

        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }

        PersonaMapper.updateEntityFromDto(dto, persona, empresa);
        Persona actualizado = personaRepository.save(persona);
        return PersonaMapper.toDto(actualizado);
    }





    @Override
    public List<PersonaResponseDto> listarPersonas() {
        return personaRepository.findAll()
                .stream().map(PersonaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaResponseDto> listarPorEstado(Persona.Estado estado) {
        return personaRepository.findByEstado(estado)
                .stream()
                .map(PersonaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarPersona(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Persona", "id", id);
        }
        personaRepository.deleteById(id);
    }

     @Override
public PersonaResponseDto cambiarEstado(Long id) {
    Persona persona = personaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));

    // Cambiar estado automáticamente
    if (persona.getEstado() == Persona.Estado.Activo) {
        persona.setEstado(Persona.Estado.Inactivo);
    } else {
        persona.setEstado(Persona.Estado.Activo);
    }

    Persona actualizado = personaRepository.save(persona);
    return PersonaMapper.toDto(actualizado);
}
}

package sigiv.Backend.sigiv.Backend.services;

import java.util.List;


import sigiv.Backend.sigiv.Backend.dto.persona.PersonaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaResponseDto;

import sigiv.Backend.sigiv.Backend.entity.Persona;

public interface PersonaService {
    PersonaResponseDto crearPersona(PersonaRequestDto dto);
    PersonaResponseDto obtenerPorId(Long id);
    PersonaResponseDto actualizarPersona(Long id, PersonaRequestDto dto);
    List<PersonaResponseDto> listarPersonas();
    List<PersonaResponseDto> listarPorEstado(Persona.Estado estado);
    void eliminarPersona(Long id);
    PersonaResponseDto cambiarEstado(Long id);
}

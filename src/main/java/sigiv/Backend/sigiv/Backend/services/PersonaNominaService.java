package sigiv.Backend.sigiv.Backend.services;

import java.util.List;


import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaRequestDto;

import sigiv.Backend.sigiv.Backend.dto.PersonaNomina.PersonaNominaResponseDto;

import sigiv.Backend.sigiv.Backend.entity.PersonaNomina;

public interface PersonaNominaService {
    PersonaNominaResponseDto crearPersonaNomina(PersonaNominaRequestDto dto);
    PersonaNominaResponseDto obtenerPorId(Long id);
    PersonaNominaResponseDto actualizarPersonaNomina(Long id, PersonaNominaRequestDto dto);
    List<PersonaNominaResponseDto> listarPersonas();
    void eliminarPersona(Long id);
    PersonaNominaResponseDto cambiarEstado(Long id);
}

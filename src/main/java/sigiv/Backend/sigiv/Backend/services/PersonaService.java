package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import org.springframework.data.domain.Page;

import sigiv.Backend.sigiv.Backend.dto.persona.PersonaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.persona.PersonaResponseDto;

import sigiv.Backend.sigiv.Backend.entity.Persona;

public interface PersonaService {
    PersonaResponseDto crearPersona(PersonaRequestDto dto);
    PersonaResponseDto obtenerPorId(Long id);
    PersonaResponseDto actualizarPersona(Long id, PersonaRequestDto dto);
    List<PersonaResponseDto> listarPersonas();
    List<PersonaResponseDto> listarPorEstado(Persona.Estado estado);
    List<PersonaResponseDto> listarPorEmpresa(Long empresaId);
    Page<PersonaResponseDto> listarPorEmpresaPaginado(Long empresaId, int page, int size);
    Page<PersonaResponseDto> filtrarPorEmpresa(Long empresaId, Persona.Estado estado, String documento, String nombre, int page, int size);
    void eliminarPersona(Long id);
    PersonaResponseDto cambiarEstado(Long id);
}

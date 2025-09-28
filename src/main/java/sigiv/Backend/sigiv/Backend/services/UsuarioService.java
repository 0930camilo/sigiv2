package sigiv.Backend.sigiv.Backend.services;

import java.util.List;

import sigiv.Backend.sigiv.Backend.dto.user.UsuarioRequestDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Usuario;

public interface UsuarioService {
    UsuarioResponseDto crearUsuario(UsuarioRequestDto dto);
    UsuarioResponseDto obtenerPorId(Long id);
    UsuarioResponseDto actualizarUsuario(Long id, UsuarioRequestDto dto);
    List<UsuarioResponseDto> listarUsuarios();
    List<UsuarioResponseDto> listarPorEstado(Usuario.Estado estado);
    void eliminarUsuario(Long id);
}

package sigiv.Backend.sigiv.Backend.services;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;


import sigiv.Backend.sigiv.Backend.dto.user.UsuarioRequestDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Usuario;

public interface UsuarioService {
    UsuarioResponseDto crearUsuario(UsuarioRequestDto dto);
    UsuarioResponseDto obtenerPorId(Long id);
    UsuarioResponseDto actualizarUsuario(Long id, UsuarioRequestDto dto);
    void eliminarUsuario(Long id);


    Page<UsuarioResponseDto> listarUsuariosPorEmpresa(Long empresaId, int page, int size, Usuario.Estado estado, String nombres);








BigDecimal calcularTotalVendido(Long idUsuario);
BigDecimal calcularTotalVendidoEntreFechas(Long idUsuario, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);
BigDecimal calcularGananciaPorUsuario(Long idUsuario);







}

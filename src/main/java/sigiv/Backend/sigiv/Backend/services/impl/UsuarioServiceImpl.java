package sigiv.Backend.sigiv.Backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import sigiv.Backend.sigiv.Backend.dto.mapper.UsuarioMapper;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioRequestDto;
import sigiv.Backend.sigiv.Backend.dto.user.UsuarioResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.UsuarioRepository;
import sigiv.Backend.sigiv.Backend.repository.VentasRepository;
import sigiv.Backend.sigiv.Backend.services.UsuarioService;


@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final VentasRepository ventasRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UsuarioResponseDto crearUsuario(UsuarioRequestDto dto) {
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }
        Usuario usuario = UsuarioMapper.toEntityForCreate(dto, empresa);
        usuario.setClave(passwordEncoder.encode(dto.getClave()));
        Usuario guardado = usuarioRepository.save(usuario);
        return UsuarioMapper.toDto(guardado);
    }


    @Override
    public UsuarioResponseDto obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return UsuarioMapper.toDto(usuario);
    }

    @Override
    public UsuarioResponseDto actualizarUsuario(Long id, UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }

        // Actualizamos los datos desde el DTO (excepto la clave)
        UsuarioMapper.updateEntityFromDto(dto, usuario, empresa);

        // Si se envió una clave nueva, la encriptamos
        if (dto.getClave() != null && !dto.getClave().isEmpty()) {
            usuario.setClave(passwordEncoder.encode(dto.getClave()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.toDto(actualizado);
    }

  



    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        usuarioRepository.deleteById(id);
    }

   

 @Override
    public BigDecimal calcularTotalVendido(Long idUsuario) {
        // Verificamos que el usuario exista
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", "id", idUsuario);
        }

        return ventasRepository.totalVendidoPorUsuario(idUsuario);
    }

    
@Override
public Page<UsuarioResponseDto> listarUsuariosPorEmpresa(
        Long empresaId,
        int page,
        int size,
        Usuario.Estado estado,
        String nombres,
        String documento
) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("idUsuario").descending());

    Page<Usuario> usuariosPage;

    boolean tieneNombre = nombres != null && !nombres.trim().isEmpty();
    boolean tieneDocumento = documento != null && !documento.trim().isEmpty();

    if (estado != null && tieneNombre && tieneDocumento) {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresaAndEstadoAndNombresContainingIgnoreCaseAndDocumentoContainingIgnoreCase(
                        empresaId, estado, nombres, documento, pageable);

    } else if (estado != null && tieneNombre) {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresaAndEstadoAndNombresContainingIgnoreCase(
                        empresaId, estado, nombres, pageable);

    } else if (estado != null && tieneDocumento) {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresaAndEstadoAndDocumentoContainingIgnoreCase(
                        empresaId, estado, documento, pageable);

    } else if (estado != null) {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresaAndEstado(
                        empresaId, estado, pageable);

    } else if (tieneNombre && tieneDocumento) {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresaAndNombresContainingIgnoreCaseAndDocumentoContainingIgnoreCase(
                        empresaId, nombres, documento, pageable);

    } else if (tieneNombre) {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresaAndNombresContainingIgnoreCase(
                        empresaId, nombres, pageable);

    } else if (tieneDocumento) {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresaAndDocumentoContainingIgnoreCase(
                        empresaId, documento, pageable);

    } else {

        usuariosPage = usuarioRepository
                .findByEmpresa_IdEmpresa(empresaId, pageable);
    }

    return usuariosPage.map(UsuarioMapper::toDto);
}

@Override
public BigDecimal calcularTotalVendidoEntreFechas(Long idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {
    if (!usuarioRepository.existsById(idUsuario)) {
        throw new ResourceNotFoundException("Usuario", "id", idUsuario);
    }

    // Convertimos LocalDate a LocalDateTime para que coincidan los tipos
    LocalDateTime inicioDelDia = fechaInicio.atStartOfDay();
    LocalDateTime finDelDia = fechaFin.atTime(23, 59, 59);

    return ventasRepository.totalVendidoPorUsuarioEntreFechas(idUsuario, inicioDelDia, finDelDia);
}


@Override
public BigDecimal calcularGananciaPorUsuario(Long idUsuario) {
    return ventasRepository.gananciaPorUsuario(idUsuario);
}

@Override
public BigDecimal calcularGananciaPorUsuarioEntreFechas(Long idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {
    LocalDateTime inicioDelDia = fechaInicio.atStartOfDay();
    LocalDateTime finDelDia = fechaFin.atTime(23, 59, 59);
    return ventasRepository.gananciaPorUsuarioEntreFechas(idUsuario, inicioDelDia, finDelDia);
}







}

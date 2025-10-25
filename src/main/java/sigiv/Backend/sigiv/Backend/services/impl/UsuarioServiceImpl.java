package sigiv.Backend.sigiv.Backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
import sigiv.Backend.sigiv.Backend.repository.CategoriaRepository;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final VentasRepository ventasRepository;
    private final CategoriaRepository categoriaRepository;


    @Override
    public UsuarioResponseDto crearUsuario(UsuarioRequestDto dto) {
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        }
        Usuario usuario = UsuarioMapper.toEntityForCreate(dto, empresa);
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

        UsuarioMapper.updateEntityFromDto(dto, usuario, empresa);
        Usuario actualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.toDto(actualizado);
    }

    @Override
    public List<UsuarioResponseDto> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream().map(UsuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponseDto> listarPorEstado(Usuario.Estado estado) {
        return usuarioRepository.findByEstado(estado)
                .stream()
                .map(UsuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        usuarioRepository.deleteById(id);
    }

     @Override
public UsuarioResponseDto cambiarEstado(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

    // Cambiar estado automáticamente
    if (usuario.getEstado() == Usuario.Estado.Activo) {
        usuario.setEstado(Usuario.Estado.Inactivo);
    } else {
        usuario.setEstado(Usuario.Estado.Activo);
    }

    Usuario actualizado = usuarioRepository.save(usuario);
    return UsuarioMapper.toDto(actualizado);
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
public List<sigiv.Backend.sigiv.Backend.entity.Categoria> listarCategoriasPorUsuario(Long idUsuario) {
    // Verificar que el usuario exista
    Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", idUsuario));

    // Obtener las categorías de la empresa a la que pertenece este usuario
    return categoriaRepository.findCategoriasByUsuario(idUsuario);
}

@Override
public List<sigiv.Backend.sigiv.Backend.dto.provee.ProveedorResponseDto> listarProveedoresPorUsuario(Long idUsuario) {
    Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", idUsuario));

    Empresa empresa = usuario.getEmpresa();
    if (empresa == null) {
        throw new IllegalArgumentException("El usuario no está asociado a ninguna empresa");
    }

    // Obtener los proveedores de la empresa del usuario
    List<sigiv.Backend.sigiv.Backend.entity.Proveedor> proveedores = empresa.getProveedores();

    return proveedores.stream()
            .map(sigiv.Backend.sigiv.Backend.dto.mapper.ProveedorMapper::toDto)
            .collect(Collectors.toList());
}

}

package sigiv.Backend.sigiv.Backend.config.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.auth.LoginRequestDto;
import sigiv.Backend.sigiv.Backend.dto.auth.LoginResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.UsuarioRepository;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {
     private final EmpresaRepository empresaRepository;
     private final UsuarioRepository usuarioRepository;
     private final PasswordEncoder passwordEncoder;
     private final JwtUtil jwtUtil;

 public LoginResponseDto login(LoginRequestDto request) {
        String username = request.getUsuario();
        String password = request.getClave();

        Optional<Empresa> empresaOpt = empresaRepository.findByNombreEmpresa(username);
        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();

            if (passwordEncoder.matches(password, empresa.getClave())) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("rol", "ROLE_EMPRESA");
                claims.put("id", empresa.getId_Empresa());
                claims.put("nombre_empresa", empresa.getNombreEmpresa());
                claims.put("estado", empresa.getEstado());
                claims.put("nit", empresa.getNit());

                String token = jwtUtil.generarToken(username, claims);

                return LoginResponseDto.builder()
                        .token(token)
                        .usuario(empresa.getNombreEmpresa())
                        .rol("ROLE_EMPRESA")
                        .build();
            }
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombres(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (passwordEncoder.matches(password, usuario.getClave())) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("rol", "ROLE_USUARIO");
                claims.put("id", usuario.getId_usuario());
                claims.put("nombre", usuario.getNombres());
                claims.put("estado", usuario.getEstado());
                claims.put("empresa_id", usuario.getEmpresa().getId_Empresa());

                String token = jwtUtil.generarToken(username, claims);

                return LoginResponseDto.builder()
                        .token(token)
                        .usuario(usuario.getNombres())
                        .rol("ROLE_USUARIO")
                        .build();
            }
        }

        throw new RuntimeException("Credenciales inválidas. Verifique nombre y clave.");
    }
}

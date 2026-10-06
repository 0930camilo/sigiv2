package sigiv.Backend.sigiv.Backend.config.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.auth.LoginRequestDto;
import sigiv.Backend.sigiv.Backend.dto.auth.LoginResponseDto;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Usuario;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.UsuarioRepository;
import sigiv.Backend.sigiv.Backend.services.security.LoginAttemptService;
import sigiv.Backend.sigiv.Backend.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;

    public LoginResponseDto login(LoginRequestDto requestDto) {
        String username = requestDto.getUsuario();
        String ip = getClientIP();
        String key = username.toLowerCase() + "@" + ip;

        if (loginAttemptService.isBlocked(key)) {
            throw new RuntimeException("Has excedido el número de intentos para este usuario. Tu acceso ha sido bloqueado por 15 minutos.");
        }

        String password = requestDto.getClave();

        Optional<Empresa> empresaOpt = empresaRepository.findByNombreEmpresa(username);
        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();

            if (!passwordEncoder.matches(password, empresa.getClave())) {
                loginAttemptService.loginFailed(key);
                throw new RuntimeException(getLoginAttemptsMessage(key));
            }

            if (empresa.getEstado() != Empresa.Estado.Activo) {
                throw new RuntimeException("La empresa está inactiva. Contacte al administrador.");
            }

            loginAttemptService.loginSucceeded(key);
            Map<String, Object> claims = new HashMap<>();
            claims.put("rol", "ROLE_EMPRESA");
            claims.put("id", empresa.getIdEmpresa());
            claims.put("nombre_empresa", empresa.getNombreEmpresa());
            claims.put("estado", empresa.getEstado());
            claims.put("nit", empresa.getNit());
            claims.put("telefono", empresa.getTelefono());
            claims.put("direccion", empresa.getDireccion());

            String token = jwtUtil.generarToken(username, claims);

            return LoginResponseDto.builder()
                    .token(token)
                    .usuario(empresa.getNombreEmpresa())
                    .rol("ROLE_EMPRESA")
                    .build();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombres(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (!passwordEncoder.matches(password, usuario.getClave())) {
                loginAttemptService.loginFailed(key);
                throw new RuntimeException(getLoginAttemptsMessage(key));
            }

            if (usuario.getEstado() != Usuario.Estado.Activo) {
                throw new RuntimeException("El usuario está inactivo. Contacte al administrador.");
            }

            Empresa empresaUsuario = usuario.getEmpresa();
            if (empresaUsuario != null && empresaUsuario.getEstado() != Empresa.Estado.Activo) {
                throw new RuntimeException("La empresa asociada está inactiva. Contacte al administrador.");
            }

            loginAttemptService.loginSucceeded(key);
            Map<String, Object> claims = new HashMap<>();
            claims.put("rol", "ROLE_USUARIO");
            claims.put("id", usuario.getIdUsuario());
            claims.put("nombre", usuario.getNombres());
            claims.put("estado", usuario.getEstado());
            claims.put("telefono", usuario.getTelefono());
            claims.put("direccion", usuario.getDireccion());
            claims.put("empresa_id", usuario.getEmpresa().getIdEmpresa());
            claims.put("empresa_nombre", usuario.getEmpresa().getNombreEmpresa());
            claims.put("empresa_nit", usuario.getEmpresa().getNit());

            String token = jwtUtil.generarToken(username, claims);

            return LoginResponseDto.builder()
                    .token(token)
                    .usuario(usuario.getNombres())
                    .rol("ROLE_USUARIO")
                    .build();
        }

        loginAttemptService.loginFailed(key);
        throw new RuntimeException(getLoginAttemptsMessage(key));
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String getLoginAttemptsMessage(String key) {
        int attempts = 0;
        try {
            attempts = loginAttemptService.getAttempts(key);
        } catch (ExecutionException e) {
            // No hacer nada, se mantendrá en 0
        }
        int remainingAttempts = LoginAttemptService.MAX_ATTEMPT - attempts;
        if (remainingAttempts <= 0) {
            return "Has excedido el número de intentos para este usuario. Tu acceso ha sido bloqueado por 15 minutos.";
        }
        return String.format("Credenciales inválidas. Te quedan %d intento(s).", remainingAttempts);
    }
}
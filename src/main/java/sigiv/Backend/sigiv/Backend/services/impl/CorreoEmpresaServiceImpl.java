package sigiv.Backend.sigiv.Backend.services.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.dto.correo.CorreoEmpresaRequestDto;
import sigiv.Backend.sigiv.Backend.dto.correo.CorreoEmpresaResponseDto;
import sigiv.Backend.sigiv.Backend.entity.CorreoEmpresa;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.exception.ResourceNotFoundException;
import sigiv.Backend.sigiv.Backend.repository.CorreoEmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.EmpresaRepository;
import sigiv.Backend.sigiv.Backend.services.CorreoEmpresaService;

@Service
@RequiredArgsConstructor
public class CorreoEmpresaServiceImpl implements CorreoEmpresaService {

    private final EmpresaRepository empresaRepository;
    private final CorreoEmpresaRepository correoEmpresaRepository;

    @Value("${mail.default.host:smtp.gmail.com}")
    private String defaultHost;

    @Value("${mail.default.port:587}")
    private Integer defaultPort;

    @Value("${mail.default.starttls:true}")
    private Boolean defaultStartTls;

    @Override
    public CorreoEmpresaResponseDto guardarConfiguracion(Long empresaId, CorreoEmpresaRequestDto dto) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", empresaId));

        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()) {
            empresa.setCorreo(dto.getCorreo().trim());
            empresaRepository.save(empresa);
        }

        if (empresa.getCorreo() == null || empresa.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo de la empresa es obligatorio");
        }

        CorreoEmpresa correoEmpresa = correoEmpresaRepository.findByEmpresaIdEmpresa(empresaId)
                .orElseGet(CorreoEmpresa::new);

        correoEmpresa.setEmpresa(empresa);

        if (dto.getClaveAplicacion() != null && !dto.getClaveAplicacion().isBlank()) {
            correoEmpresa.setClaveAplicacion(dto.getClaveAplicacion().trim());
        }

        if (correoEmpresa.getClaveAplicacion() == null || correoEmpresa.getClaveAplicacion().isBlank()) {
            throw new IllegalArgumentException("La clave de aplicación es obligatoria");
        }

        correoEmpresa.setSmtpHost(valorONull(dto.getSmtpHost(), defaultHost));
        correoEmpresa.setSmtpPort(dto.getSmtpPort() != null ? dto.getSmtpPort() : defaultPort);
        correoEmpresa.setStartTls(dto.getStartTls() != null ? dto.getStartTls() : defaultStartTls);
        correoEmpresa.setActualizadoEn(LocalDateTime.now());

        return toDto(correoEmpresaRepository.save(correoEmpresa));
    }

    @Override
    public CorreoEmpresaResponseDto obtenerConfiguracion(Long empresaId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", empresaId));

        return correoEmpresaRepository.findByEmpresaIdEmpresa(empresaId)
                .map(this::toDto)
                .orElseGet(() -> new CorreoEmpresaResponseDto(
                        empresa.getIdEmpresa(),
                        empresa.getCorreo(),
                        defaultHost,
                        defaultPort,
                        defaultStartTls,
                        false,
                        null
                ));
    }

    private CorreoEmpresaResponseDto toDto(CorreoEmpresa correoEmpresa) {
        Empresa empresa = correoEmpresa.getEmpresa();
        return new CorreoEmpresaResponseDto(
                empresa.getIdEmpresa(),
                empresa.getCorreo(),
                correoEmpresa.getSmtpHost(),
                correoEmpresa.getSmtpPort(),
                correoEmpresa.getStartTls(),
                correoEmpresa.getClaveAplicacion() != null && !correoEmpresa.getClaveAplicacion().isBlank(),
                correoEmpresa.getActualizadoEn()
        );
    }

    private String valorONull(String value, String fallback) {
        return value != null && !value.isBlank() ? value.trim() : fallback;
    }
}

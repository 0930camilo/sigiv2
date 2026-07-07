package sigiv.Backend.sigiv.Backend.services.impl;

import java.util.Properties;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import sigiv.Backend.sigiv.Backend.entity.CorreoEmpresa;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.entity.Ventas;
import sigiv.Backend.sigiv.Backend.repository.CorreoEmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.VentasRepository;
import sigiv.Backend.sigiv.Backend.services.FacturaEmailService;
import sigiv.Backend.sigiv.Backend.services.VentasService;

@Service
@RequiredArgsConstructor
public class FacturaEmailServiceImpl implements FacturaEmailService {

    private final VentasService ventasService;
    private final VentasRepository ventasRepository;
    private final CorreoEmpresaRepository correoEmpresaRepository;

    @Override
    public void enviarFacturaPorCorreo(Long ventaId, String correoDestino) {
        if (correoDestino == null || correoDestino.isBlank()) {
            throw new IllegalArgumentException("El correo destino es obligatorio");
        }

        Ventas venta = ventasRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        Empresa empresa = venta.getUsuario() != null ? venta.getUsuario().getEmpresa() : null;
        if (empresa == null) {
            throw new IllegalArgumentException("La venta no tiene empresa asociada");
        }

        if (empresa.getCorreo() == null || empresa.getCorreo().isBlank()) {
            throw new IllegalArgumentException("La empresa no tiene correo configurado");
        }

        CorreoEmpresa configuracionCorreo = correoEmpresaRepository.findByEmpresaIdEmpresa(empresa.getIdEmpresa())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La empresa no tiene clave de aplicación configurada para facturación"
                ));

        if (configuracionCorreo.getClaveAplicacion() == null || configuracionCorreo.getClaveAplicacion().isBlank()) {
            throw new IllegalArgumentException("La clave de aplicación de la empresa es obligatoria");
        }

        byte[] pdf = ventasService.generarFacturaPdf(ventaId);
        enviarCorreo(venta, empresa, configuracionCorreo, correoDestino.trim(), pdf);
    }

    private void enviarCorreo(
            Ventas venta,
            Empresa empresa,
            CorreoEmpresa configuracionCorreo,
            String correoDestino,
            byte[] pdf
    ) {
        try {
            JavaMailSenderImpl mailSender = crearMailSender(empresa, configuracionCorreo);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String nombreEmpresa = empresa.getNombreEmpresa() != null && !empresa.getNombreEmpresa().isBlank()
                    ? empresa.getNombreEmpresa()
                    : "SIGIV";
            String nombreCliente = venta.getNombreCliente() != null && !venta.getNombreCliente().isBlank()
                    ? venta.getNombreCliente()
                    : "cliente";

            helper.setFrom(empresa.getCorreo(), nombreEmpresa);
            helper.setTo(correoDestino);
            helper.setSubject("Factura de venta #" + venta.getIdventa());
            helper.setText(
                    "Hola " + nombreCliente + ",\n\n"
                            + "Adjuntamos la factura de tu compra realizada en " + nombreEmpresa + ".\n\n"
                            + "Gracias por tu compra.",
                    false
            );

            helper.addAttachment(
                    "factura-" + venta.getIdventa() + ".pdf",
                    () -> new java.io.ByteArrayInputStream(pdf)
            );

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error preparando el correo de la factura", e);
        } catch (MailException e) {
            throw new RuntimeException("Error enviando la factura por correo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado enviando la factura por correo", e);
        }
    }

    private JavaMailSenderImpl crearMailSender(Empresa empresa, CorreoEmpresa configuracionCorreo) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configuracionCorreo.getSmtpHost());
        mailSender.setPort(configuracionCorreo.getSmtpPort());
        mailSender.setUsername(empresa.getCorreo());
        mailSender.setPassword(configuracionCorreo.getClaveAplicacion());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(Boolean.TRUE.equals(configuracionCorreo.getStartTls())));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        return mailSender;
    }
}

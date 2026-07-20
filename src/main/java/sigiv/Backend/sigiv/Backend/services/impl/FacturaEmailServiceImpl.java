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
import java.net.InetSocketAddress;
import java.net.Socket;


@Service
@RequiredArgsConstructor
public class FacturaEmailServiceImpl implements FacturaEmailService {

    private final VentasService ventasService;
    private final VentasRepository ventasRepository;
    private final CorreoEmpresaRepository correoEmpresaRepository;

    @Override
    public void enviarFacturaPorCorreo(Long ventaId, String correoDestino) {
        enviarFacturaPorCorreo(ventaId, correoDestino, null);
    }

    @Override
    public void enviarFacturaPorCorreo(Long ventaId, String correoDestino, String formatoFactura) {
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

        boolean formatoPos = "POS".equalsIgnoreCase(formatoFactura);
        byte[] pdf = formatoPos
                ? ventasService.generarFacturaPosPdf(ventaId)
                : ventasService.generarFacturaPdf(ventaId);
        enviarCorreo(venta, empresa, configuracionCorreo, correoDestino.trim(), pdf, formatoPos);
    }

    private void enviarCorreo(
            Ventas venta,
            Empresa empresa,
            CorreoEmpresa configuracionCorreo,
            String correoDestino,
            byte[] pdf,
            boolean formatoPos
    ) {
        try {

            System.out.println("========== CONFIG SMTP ==========");
            System.out.println("Host: " + configuracionCorreo.getSmtpHost());
            System.out.println("Puerto: " + configuracionCorreo.getSmtpPort());
            System.out.println("TLS: " + configuracionCorreo.getStartTls());
            System.out.println("Correo: " + empresa.getCorreo());
            System.out.println("Password vacía: " +
                    (configuracionCorreo.getClaveAplicacion() == null
                            || configuracionCorreo.getClaveAplicacion().isBlank()));
            System.out.println("=================================");

            JavaMailSenderImpl mailSender = crearMailSender(empresa, configuracionCorreo);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // resto de tu código...
            String nombreEmpresa = empresa.getNombreEmpresa() != null && !empresa.getNombreEmpresa().isBlank()
                    ? empresa.getNombreEmpresa()
                    : "SIGIV";
            String nombreCliente = venta.getNombreCliente() != null && !venta.getNombreCliente().isBlank()
                    ? venta.getNombreCliente()
                    : "cliente";

            helper.setFrom(empresa.getCorreo(), nombreEmpresa);
            helper.setTo(correoDestino);
            helper.setSubject((formatoPos ? "Factura POS #" : "Factura de venta #") + venta.getIdventa());
            helper.setText(
                    "Hola " + nombreCliente + ",\n\n"
                            + "Adjuntamos la factura "
                            + (formatoPos ? "POS " : "")
                            + "de tu compra realizada en " + nombreEmpresa + ".\n\n"
                            + "Gracias por tu compra.",
                    false
            );

            helper.addAttachment(
                    (formatoPos ? "factura-pos-" : "factura-") + venta.getIdventa() + ".pdf",
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
        props.put("mail.smtp.connectiontimeout", "60000");
        props.put("mail.smtp.timeout", "60000");
        props.put("mail.smtp.writetimeout", "60000");

        return mailSender;
    }


    @Override
    public void probarConexionSmtp() {

        System.out.println("========== PRUEBA SMTP ==========");

        try (Socket socket = new Socket()) {

            System.out.println("Intentando conectar a smtp.gmail.com:587 ...");

            socket.connect(new InetSocketAddress("smtp.gmail.com", 587), 10000);

            System.out.println("✅ Conexión TCP exitosa con Gmail.");

        } catch (Exception e) {

            System.out.println("❌ Error conectando con Gmail:");
            e.printStackTrace();

            throw new RuntimeException(e);
        }

        System.out.println("=================================");
    }
}

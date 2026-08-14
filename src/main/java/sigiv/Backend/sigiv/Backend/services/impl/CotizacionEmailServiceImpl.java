package sigiv.Backend.sigiv.Backend.services.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import sigiv.Backend.sigiv.Backend.entity.CorreoEmpresa;
import sigiv.Backend.sigiv.Backend.entity.Cotizacion;
import sigiv.Backend.sigiv.Backend.entity.DetalleCotizacion;
import sigiv.Backend.sigiv.Backend.entity.Empresa;
import sigiv.Backend.sigiv.Backend.repository.CorreoEmpresaRepository;
import sigiv.Backend.sigiv.Backend.repository.CotizacionRepository;
import sigiv.Backend.sigiv.Backend.services.CotizacionEmailService;

@Service
@RequiredArgsConstructor
public class CotizacionEmailServiceImpl implements CotizacionEmailService {

    private final CotizacionRepository cotizacionRepository;
    private final CorreoEmpresaRepository correoEmpresaRepository;

    @Override
    public void enviarCotizacionPorCorreo(
            Long cotizacionId,
            String correoDestino
    ) {

        if (correoDestino == null || correoDestino.isBlank()) {
            throw new IllegalArgumentException("El correo destino es obligatorio");
        }

        Cotizacion cotizacion = cotizacionRepository
                .findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        Empresa empresa = cotizacion.getUsuario() != null
                ? cotizacion.getUsuario().getEmpresa()
                : null;

        if (empresa == null) {
            throw new IllegalArgumentException("La cotización no tiene empresa asociada");
        }

        if (empresa.getCorreo() == null || empresa.getCorreo().isBlank()) {
            throw new IllegalArgumentException("La empresa no tiene correo configurado");
        }

        CorreoEmpresa configuracionCorreo = correoEmpresaRepository
                .findByEmpresaIdEmpresa(empresa.getIdEmpresa())
                .orElseThrow(() -> new IllegalArgumentException("La empresa no tiene clave de aplicación configurada para cotizaciones"));

        if (configuracionCorreo.getClaveAplicacion() == null || configuracionCorreo.getClaveAplicacion().isBlank()) {
            throw new IllegalArgumentException("La clave de aplicación de la empresa es obligatoria");
        }

        byte[] pdf = generarCotizacionPosPdf(cotizacionId); // Llamada al método privado

        enviarCorreo(cotizacion, empresa, configuracionCorreo, correoDestino.trim(), pdf);
    }

    private byte[] generarCotizacionPosPdf(Long id) {
        try {
            Cotizacion cotizacion = cotizacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

            Empresa empresa = cotizacion.getUsuario().getEmpresa();
            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es", "CO"));
            formatoNumero.setMinimumFractionDigits(0);
            formatoNumero.setMaximumFractionDigits(0);

            int cantidadDetalles = cotizacion.getDetalles() != null ? cotizacion.getDetalles().size() : 0;
            float altoPagina = Math.max(420f, 300f + (cantidadDetalles * 46f));
            com.lowagie.text.Rectangle pageSize = new com.lowagie.text.Rectangle(226.77f, altoPagina);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(pageSize, 8f, 8f, 8f, 8f);
            com.lowagie.text.pdf.PdfWriter.getInstance(document, out);

            document.open();

            Font tituloFont = new Font(Font.COURIER, 10, Font.BOLD);
            Font normalFont = new Font(Font.COURIER, 8, Font.NORMAL);
            Font boldFont = new Font(Font.COURIER, 8, Font.BOLD);

            addCentered(document, safeText(empresa.getNombreEmpresa(), "SIGIV"), tituloFont);
            addCentered(document, "COTIZACIÓN POS", boldFont);
            addCentered(document, "No. " + cotizacion.getIdcotizacion(), normalFont);
            addLine(document, "--------------------------------", normalFont);
            addLine(document, "Fecha: " + safeText(cotizacion.getFecha(), "-"), normalFont);
            addLine(document, "Cliente: " + safeText(cotizacion.getNombreCliente(), "Cliente"), normalFont);
            addLine(document, "Vendedor: " + safeText(cotizacion.getUsuario().getNombres(), "-"), normalFont);
            addLine(document, "--------------------------------", normalFont);

            if (cotizacion.getDetalles() != null) {
                for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
                    String nombreProducto = detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto";
                    BigDecimal precioProducto = detalle.getPrecio() != null ? detalle.getPrecio() : BigDecimal.ZERO;
                    BigDecimal subtotal = detalle.getSubtotal() != null ? detalle.getSubtotal() : BigDecimal.ZERO;
                    String precio = "$" + formatoNumero.format(precioProducto);
                    String subtotalTexto = "$" + formatoNumero.format(subtotal);
                    addLine(document, limitar(nombreProducto, 32), boldFont);
                    addLine(document, detalle.getCantidad() + " x " + precio + " = " + subtotalTexto, normalFont);
                }
            }

            addLine(document, "--------------------------------", normalFont);
            addLine(document, "TOTAL: $" + formatoNumero.format(valorSeguro(cotizacion.getTotal())), boldFont);
            addLine(document, "--------------------------------", normalFont);
            addCentered(document, "Cotización no reserva inventario", normalFont);
            addCentered(document, "Gracias por su preferencia", normalFont);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando cotización POS PDF", e);
        }
    }

    private void enviarCorreo(Cotizacion cotizacion, Empresa empresa, CorreoEmpresa configuracionCorreo, String correoDestino, byte[] pdf) {
        try {
            JavaMailSenderImpl mailSender = crearMailSender(empresa, configuracionCorreo);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String nombreEmpresa = empresa.getNombreEmpresa() != null && !empresa.getNombreEmpresa().isBlank() ? empresa.getNombreEmpresa() : "SIGIV";
            String nombreCliente = cotizacion.getNombreCliente() != null && !cotizacion.getNombreCliente().isBlank() ? cotizacion.getNombreCliente() : "cliente";

            helper.setFrom(empresa.getCorreo(), nombreEmpresa);
            helper.setTo(correoDestino);
            helper.setSubject("Cotización POS #" + cotizacion.getIdcotizacion());
            helper.setText("Hola " + nombreCliente + ",\n\nAdjuntamos la cotización POS #" + cotizacion.getIdcotizacion() + " realizada en " + nombreEmpresa + ".\n\nLa cotización se adjunta en formato PDF POS.\n\nGracias por tu interés.\n\n" + nombreEmpresa, false);
            helper.addAttachment("cotizacion-pos-" + cotizacion.getIdcotizacion() + ".pdf", () -> new java.io.ByteArrayInputStream(pdf));

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Error enviando la cotización por correo", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado enviando la cotización por correo", e);
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

        if (configuracionCorreo.getSmtpPort() == 465) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.starttls.enable", "false");
        } else {
            props.put("mail.smtp.starttls.enable", String.valueOf(Boolean.TRUE.equals(configuracionCorreo.getStartTls())));
        }

        props.put("mail.smtp.connectiontimeout", "60000");
        props.put("mail.smtp.timeout", "60000");
        props.put("mail.smtp.writetimeout", "60000");

        return mailSender;
    }

    // Métodos auxiliares para la generación de PDF
    private void addCentered(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setLeading(10f);
        document.add(paragraph);
    }

    private void addLine(Document document, String text, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setLeading(10f);
        document.add(paragraph);
    }

    private String safeText(Object value, String fallback) {
        if (value == null || String.valueOf(value).isBlank()) {
            return fallback;
        }
        return String.valueOf(value);
    }

    private String limitar(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private BigDecimal valorSeguro(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
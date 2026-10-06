package sigiv.Backend.sigiv.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigiv.Backend.sigiv.Backend.services.impl.BrevoEmailServiceImpl;

import java.util.Map;

@RestController
@RequestMapping("/api/test/email")
public class EmailTestController {

    @Autowired
    private BrevoEmailServiceImpl brevoEmailService;

    @PostMapping("/brevo")
    public ResponseEntity<?> probarBrevo(@RequestBody Map<String, Object> request) {
        try {
            String apiKey = (String) request.get("apiKey");
            String senderName = (String) request.get("senderName");
            String senderEmail = (String) request.get("senderEmail");
            String recipientEmail = (String) request.get("recipientEmail");
            String subject = (String) request.get("subject");
            String textContent = (String) request.get("textContent");
            String fileName = (String) request.get("fileName");
            String dummyContent = "Este es un contenido de prueba para el adjunto.";
            byte[] attachmentContent = (request.get("attachmentText") != null) 
                    ? ((String) request.get("attachmentText")).getBytes() 
                    : dummyContent.getBytes();

            brevoEmailService.enviarCorreoConAdjunto(
                    apiKey,
                    senderName,
                    senderEmail,
                    recipientEmail,
                    subject,
                    textContent,
                    fileName != null ? fileName : "prueba.txt",
                    attachmentContent
            );

            return ResponseEntity.ok(Map.of("success", true, "message", "Correo enviado exitosamente a través de Brevo"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false, 
                    "message", "Error al enviar correo: " + e.getMessage()
            ));
        }
    }
}

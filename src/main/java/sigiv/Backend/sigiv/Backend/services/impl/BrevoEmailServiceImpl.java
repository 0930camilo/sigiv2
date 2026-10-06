package sigiv.Backend.sigiv.Backend.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class BrevoEmailServiceImpl {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public void enviarCorreoConAdjunto(
            String apiKey,
            String senderName,
            String senderEmail,
            String recipientEmail,
            String subject,
            String textContent,
            String fileName,
            byte[] attachmentContent
    ) {
        try {
            String cleanApiKey = apiKey.trim();
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", cleanApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, Object> requestBody = new HashMap<>();
            
            // Remitente
            Map<String, String> sender = new HashMap<>();
            sender.put("name", senderName);
            sender.put("email", senderEmail);
            requestBody.put("sender", sender);

            // Destinatarios
            List<Map<String, String>> to = new ArrayList<>();
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", recipientEmail);
            to.add(recipient);
            requestBody.put("to", to);

            // Contenido
            requestBody.put("subject", subject);
            requestBody.put("textContent", textContent);

            // Adjunto (Base64)
            List<Map<String, String>> attachments = new ArrayList<>();
            Map<String, String> attachment = new HashMap<>();
            attachment.put("name", fileName);
            attachment.put("content", Base64.getEncoder().encodeToString(attachmentContent));
            attachments.add(attachment);
            requestBody.put("attachments", attachments);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            restTemplate.postForEntity(BREVO_API_URL, entity, String.class);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo a través de la API de Brevo: " + e.getMessage(), e);
        }
    }
}

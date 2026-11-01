package com.vocab.service;

import com.vocab.dto.ChangeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.notification.email:admin@example.com}")
    private String recipientEmail;

    public void sendChangeNotification(ChangeMessage message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom("vocab-app@notification.com");
            mail.setTo(recipientEmail);
            mail.setSubject("📚 Изменение в словаре: " + message.getChangeType() + " " + message.getEntityType());

            String body = String.format(
                    "Здравствуйте!\n\n" +
                            "В системе учета английских слов произошло изменение:\n\n" +
                            "• Тип изменения: %s\n" +
                            "• Сущность: %s\n" +
                            "• ID: %d\n" +
                            "• Поле: %s\n" +
                            "• Старое значение: %s\n" +
                            "• Новое значение: %s\n" +
                            "• Время: %s\n\n" +
                            "С уважением,\nСистема VocabApp",
                    message.getChangeType(),
                    message.getEntityType(),
                    message.getEntityId(),
                    message.getFieldName(),
                    message.getOldValue() != null ? message.getOldValue() : "—",
                    message.getNewValue() != null ? message.getNewValue() : "—",
                    message.getTimestamp()
            );
            mail.setText(body);

            mailSender.send(mail);
            log.info(">>> [EMAIL] Уведомление отправлено на {}", recipientEmail);
        } catch (Exception e) {
            log.error(">>> [EMAIL] Ошибка отправки email", e);
        }
    }
}
package com.vocab.service;

import com.vocab.dto.ChangeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsNotificationService {

    private static final Logger log = LoggerFactory.getLogger(JmsNotificationService.class);

    public static final String AUDIT_QUEUE = "audit.queue";
    public static final String EMAIL_QUEUE = "email.queue";

    @Autowired
    private JmsTemplate jmsTemplate;

    // Отправить сообщение в очередь аудита (всегда)
    public void sendAuditMessage(ChangeMessage message) {
        log.info(">>> Отправка JMS сообщения в аудит: {} {} #{}",
                message.getChangeType(), message.getEntityType(), message.getEntityId());
        jmsTemplate.convertAndSend(AUDIT_QUEUE, message);
    }

    // Отправить сообщение в очередь email (только для выбранных событий)
    public void sendEmailNotification(ChangeMessage message) {
        log.info(">>> Отправка JMS сообщения для email: {} {} #{}",
                message.getChangeType(), message.getEntityType(), message.getEntityId());
        jmsTemplate.convertAndSend(EMAIL_QUEUE, message);
    }
}
package com.vocab.jms;

import com.vocab.dto.ChangeMessage;
import com.vocab.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * MDP для обработки email-уведомлений.
 * Проверяет условие: отправляем email только при INSERT или DELETE для Word.
 */
@Component
public class EmailMessageListener {

    private static final Logger log = LoggerFactory.getLogger(EmailMessageListener.class);

    @Autowired
    private EmailService emailService;

    @JmsListener(destination = "email.queue", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(ChangeMessage message) {
        log.info(">>> [EMAIL MDP] Получено сообщение: {} {} #{}",
                message.getChangeType(), message.getEntityType(), message.getEntityId());

        // Задание 5: фильтрация событий
        boolean shouldNotify = isNotificationRequired(message);

        if (shouldNotify) {
            log.info(">>> [EMAIL MDP] Условие выполнено, отправляем email");
            emailService.sendChangeNotification(message);
        } else {
            log.info(">>> [EMAIL MDP] Условие не выполнено, email не отправляется");
        }
    }

    /**
     * Проверяет, нужно ли отправлять уведомление для данного события.
     * Условие: только INSERT и DELETE для сущности Word.
     */
    private boolean isNotificationRequired(ChangeMessage message) {
        if (message.getEntityType() == null) return false;

        // Уведомляем о создании и удалении слов
        if ("Word".equals(message.getEntityType())) {
            return "INSERT".equals(message.getChangeType()) ||
                    "DELETE".equals(message.getChangeType());
        }

        // Можно добавить другие условия, например:
        // if ("Category".equals(message.getEntityType())) {
        //     return "INSERT".equals(message.getChangeType());
        // }

        return false;
    }
}
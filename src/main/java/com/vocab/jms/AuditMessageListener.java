package com.vocab.jms;

import com.vocab.dto.ChangeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * MDP (Message-Driven POJO) — аналог MDB из EJB.
 * Слушает очередь audit.queue и записывает изменения в таблицу audit_log.
 */
@Component
public class AuditMessageListener {

    private static final Logger log = LoggerFactory.getLogger(AuditMessageListener.class);

    @Autowired
    private DataSource dataSource;

    @JmsListener(destination = "audit.queue", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(ChangeMessage message) {
        log.info(">>> [AUDIT] Получено сообщение: {} {} #{}",
                message.getChangeType(), message.getEntityType(), message.getEntityId());

        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO audit_log (change_type, entity_type, entity_id, field_name, old_value, new_value, changed_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, message.getChangeType());
            ps.setString(2, message.getEntityType());
            ps.setLong(3, message.getEntityId() != null ? message.getEntityId() : 0);
            ps.setString(4, message.getFieldName());
            ps.setString(5, message.getOldValue());
            ps.setString(6, message.getNewValue());
            ps.setTimestamp(7, Timestamp.valueOf(message.getTimestamp()));
            ps.executeUpdate();

            log.info(">>> [AUDIT] Запись успешно сохранена в БД");
        } catch (Exception e) {
            log.error(">>> [AUDIT] Ошибка при записи в audit_log", e);
        }
    }
}
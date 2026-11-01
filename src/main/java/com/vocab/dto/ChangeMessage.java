package com.vocab.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChangeMessage implements Serializable {
    private String changeType;    // INSERT, UPDATE, DELETE
    private String entityType;    // Word, Category, Translation
    private Long entityId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;

    // Пустой конструктор для Jackson
    public ChangeMessage() {}

    public ChangeMessage(String changeType, String entityType, Long entityId,
                         String fieldName, String oldValue, String newValue) {
        this.changeType = changeType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
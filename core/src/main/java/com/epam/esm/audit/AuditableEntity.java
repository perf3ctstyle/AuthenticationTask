package com.epam.esm.audit;

import com.epam.esm.entity.Identifiable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AuditableEntity<T extends Identifiable> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "operation_timestamp", columnDefinition = "VARCHAR(30)")
    private LocalDateTime operationTimestamp;

    public AuditableEntity() {
    }

    public AuditableEntity(T entity) {
        entityId = entity.getId();
    }

    public AuditableEntity(Long entityId, String operationType, LocalDateTime operationTimestamp) {
        this.entityId = entityId;
        this.operationType = operationType;
        this.operationTimestamp = operationTimestamp;
    }

    public AuditableEntity(Long id, Long entityId, String operationType, LocalDateTime operationTimestamp) {
        this(entityId, operationType, operationTimestamp);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getOperationTimestamp() {
        return operationTimestamp;
    }

    public void setOperationTimestamp(LocalDateTime operationTimestamp) {
        this.operationTimestamp = operationTimestamp;
    }
}

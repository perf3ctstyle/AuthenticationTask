package com.epam.esm.audit;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Identifiable;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

import static com.epam.esm.constant.GenericConstants.DATE_TIME_PATTERN;

@Component
public class AuditListener {

    private static EntityManager entityManager;

    private static final String PERSIST = "PERSIST";
    private static final String UPDATE = "UPDATE";
    private static final String REMOVE = "REMOVE";

    private static final String ENTITY_NOT_AUDITABLE = "This entity is not auditable.";

    public AuditListener() {
    }

    @Autowired
    public void setAuditDao(EntityManager entityManager) {
        AuditListener.entityManager = entityManager;
    }

    @PostPersist
    public void doAfterPersisting(Identifiable identifiable) {
        persistAuditableEntityWithOperation(identifiable, PERSIST);
    }

    @PreUpdate
    public void doBeforeUpdating(Identifiable identifiable) {
        persistAuditableEntityWithOperation(identifiable, UPDATE);
    }

    @PreRemove
    public void doBeforeRemoving(Identifiable identifiable) {
        persistAuditableEntityWithOperation(identifiable, REMOVE);
    }

    private void setGenericAuditParameters(AuditableEntity<?> auditableEntity) {
        LocalDateTime operationTimestamp = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        auditableEntity.setOperationTimestamp(operationTimestamp);
    }

    private void persistAuditableEntityWithOperation(Identifiable identifiable, String operation) {
        AuditableEntity<?> auditable = createAuditableEntity(identifiable);
        setGenericAuditParameters(auditable);
        auditable.setOperationType(operation);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(auditable);
        transaction.commit();
    }

    private AuditableEntity<?> createAuditableEntity(Identifiable identifiable) {
        if (identifiable.getClass() == GiftCertificate.class) {
            return new AuditableGiftCertificate((GiftCertificate) identifiable);
        }
        if (identifiable.getClass() == Tag.class) {
            return new AuditableTag((Tag) identifiable);
        }
        if (identifiable.getClass() == User.class) {
            return new AuditableUser((User) identifiable);
        }
        if (identifiable.getClass() == UserOrder.class) {
            return new AuditableUserOrder((UserOrder) identifiable);
        }
        throw new IllegalArgumentException(ENTITY_NOT_AUDITABLE);
    }
}
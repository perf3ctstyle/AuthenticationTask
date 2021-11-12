package com.epam.esm.audit;

import com.epam.esm.entity.GiftCertificate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "gift_certificate_audit")
public class AuditableGiftCertificate extends AuditableEntity<GiftCertificate> {

    public AuditableGiftCertificate() {
    }

    public AuditableGiftCertificate(GiftCertificate giftCertificate) {
        super(giftCertificate);
    }
}

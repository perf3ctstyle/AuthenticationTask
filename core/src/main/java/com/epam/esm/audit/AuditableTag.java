package com.epam.esm.audit;

import com.epam.esm.entity.Tag;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag_audit")
public class AuditableTag extends AuditableEntity<Tag> {

    public AuditableTag() {
    }

    public AuditableTag(Tag tag) {
        super(tag);
    }
}

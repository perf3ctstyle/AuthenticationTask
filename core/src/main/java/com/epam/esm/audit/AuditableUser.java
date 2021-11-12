package com.epam.esm.audit;

import com.epam.esm.entity.User;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_audit")
public class AuditableUser extends AuditableEntity<User> {

    public AuditableUser() {
    }

    public AuditableUser(User user) {
        super(user);
    }
}

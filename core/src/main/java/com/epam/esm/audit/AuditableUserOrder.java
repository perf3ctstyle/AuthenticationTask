package com.epam.esm.audit;

import com.epam.esm.entity.UserOrder;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_order_audit")
public class AuditableUserOrder extends AuditableEntity<UserOrder> {

    public AuditableUserOrder() {
    }

    public AuditableUserOrder(UserOrder userOrder) {
        super(userOrder);
    }
}

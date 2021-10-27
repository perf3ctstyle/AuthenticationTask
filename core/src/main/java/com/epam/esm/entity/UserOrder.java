package com.epam.esm.entity;

import com.epam.esm.audit.AuditListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_order")
@EntityListeners(AuditListener.class)
public class UserOrder extends RepresentationModel<UserOrder> implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "gift_certificate_id")
    private Long giftCertificateId;

    @Column(columnDefinition = "BIGINT")
    private Integer cost;

    @Column(name = "purchase_timestamp", columnDefinition = "VARCHAR(30)")
    private LocalDateTime purchaseTimestamp;

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public UserOrder() {
    }

    public UserOrder(Long userId, Long giftCertificateId) {
        this.userId = userId;
        this.giftCertificateId = giftCertificateId;
    }

    public UserOrder(Long userId, Long giftCertificateId, Integer cost, LocalDateTime purchaseTimestamp) {
        this(userId, giftCertificateId);
        this.cost = cost;
        this.purchaseTimestamp = purchaseTimestamp;
    }

    public UserOrder(Long id, Long userId, Long giftCertificateId, Integer cost, LocalDateTime purchaseTimestamp) {
        this(userId, giftCertificateId, cost, purchaseTimestamp);
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGiftCertificateId() {
        return giftCertificateId;
    }

    public void setGiftCertificateId(Long giftCertificateId) {
        this.giftCertificateId = giftCertificateId;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    public void setPurchaseTimestamp(LocalDateTime purchaseTimestamp) {
        this.purchaseTimestamp = purchaseTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserOrder userOrder = (UserOrder) o;
        return Objects.equals(id, userOrder.id)
                && cost.equals(userOrder.cost)
                && purchaseTimestamp.equals(userOrder.purchaseTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cost, purchaseTimestamp);
    }
}

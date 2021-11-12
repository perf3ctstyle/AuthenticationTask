package com.epam.esm.entity;

import com.epam.esm.audit.AuditListener;
import com.epam.esm.util.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.epam.esm.constant.GenericConstants.DATE_TIME_PATTERN;

@Entity
@Table(name = "gift_certificate")
@EntityListeners(AuditListener.class)
public class GiftCertificate extends RepresentationModel<GiftCertificate> implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String name;
    private String description;

    @Column(columnDefinition = "BIGINT")
    private Integer price;
    private Long duration;

    @Column(name = "create_date", columnDefinition = "VARCHAR(30)")
    private LocalDateTime createDate;

    @Column(name = "last_update_date", columnDefinition = "VARCHAR(30)")
    private LocalDateTime lastUpdateDate;

    @ManyToMany(cascade =
            {CascadeType.PERSIST,
                    CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(name = "gift_and_tag",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String DURATION = "duration";
    private static final String CREATE_DATE = "createDate";
    private static final String LAST_UPDATE_DATE = "lastUpdateDate";

    public GiftCertificate() {
    }

    public GiftCertificate(Long id,
                           String name,
                           String description,
                           Integer price,
                           Long duration,
                           LocalDateTime createDate,
                           LocalDateTime lastUpdateDate) {
        Validate.notBlank(name);
        Validate.notBlank(description);
        Validate.isTrue(price > 0 && duration > 0);

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public GiftCertificate(Long id,
                           String name,
                           String description,
                           Integer price,
                           Long duration,
                           LocalDateTime createDate,
                           LocalDateTime lastUpdateDate,
                           List<Tag> tags) {
        this(id, name, description, price, duration, createDate, lastUpdateDate);
        this.tags = tags;
    }

    public GiftCertificate(GiftCertificate giftCertificate) {
        this(giftCertificate.getId(),
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate(),
                giftCertificate.getTags()
        );
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        return duration;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonProperty
    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GiftCertificate that = (GiftCertificate) o;
        return Objects.equals(id, that.id)
                && name.equals(that.name)
                && description.equals(that.description)
                && price.equals(that.price)
                && duration.equals(that.duration)
                && createDate.equals(that.createDate)
                && lastUpdateDate.equals(that.lastUpdateDate)
                && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, duration, createDate, lastUpdateDate, tags);
    }
    
    public void updateValuesFromMap(Map<String, String> values) {
        values.computeIfPresent(NAME, (key, value) -> {
            Validate.notBlank(value);
            setName(value);
            return value;
        });

        values.computeIfPresent(DESCRIPTION, (key, value) -> {
            Validate.notBlank(value);
            setDescription(value);
            return value;
        });

        values.computeIfPresent(PRICE, (key, value) -> {
            int price = Integer.parseInt(value);
            Validate.isTrue(price > 0);
            setPrice(price);
            return value;
        });

        values.computeIfPresent(DURATION, (key, value) -> {
            long duration = Long.parseLong(value);
            Validate.isTrue(duration > 0);
            setDuration(duration);
            return value;
        });

        values.computeIfPresent(CREATE_DATE, (key, value) -> {
            LocalDateTime createDate = DateTimeUtils.of(value, DATE_TIME_PATTERN);
            setCreateDate(createDate);
            return value;
        });

        values.computeIfPresent(LAST_UPDATE_DATE, (key, value) -> {
            LocalDateTime lastUpdateDate = DateTimeUtils.of(value, DATE_TIME_PATTERN);
            setLastUpdateDate(lastUpdateDate);
            return value;
        });
    }
}

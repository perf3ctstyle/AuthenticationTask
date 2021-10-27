create table gift_certificate
(
    id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(50),
    description      VARCHAR(100),
    price            BIGINT,
    duration         BIGINT,
    create_date      VARCHAR(30),
    last_update_date VARCHAR(30)
);

create table tag
(
    id   BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);

create table gifts_and_tags
(
    certificate_id BIGINT NOT NULL,
    tag_id         BIGINT NOT NULL,
    PRIMARY KEY (certificate_id, tag_id),
    FOREIGN KEY (certificate_id) REFERENCES gift_certificate (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);

create table user
(
    id       BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    login    VARCHAR(30) NOT NULL,
    password VARCHAR(30) NOT NULL
);

create table role
(
    id   BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL
);

create table user_role
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (role_id) REFERENCES role (id)
);

create table user_order
(
    id                  BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    gift_certificate_id BIGINT NOT NULL,
    cost                BIGINT,
    purchase_timestamp  VARCHAR(30),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (gift_certificate_id) REFERENCES gift_certificate (id)
);

create table gift_certificate_audit
(
    id                  BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id           BIGINT NOT NULL,
    operation_type      VARCHAR(10),
    operation_timestamp VARCHAR(30)
);

create table tag_audit
(
    id                  BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id           BIGINT NOT NULL,
    operation_type      VARCHAR(10),
    operation_timestamp VARCHAR(30)
);

create table user_audit
(
    id                  BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id           BIGINT NOT NULL,
    operation_type      VARCHAR(10),
    operation_timestamp VARCHAR(30)
);

create table user_order_audit
(
    id                  BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id           BIGINT NOT NULL,
    operation_type      VARCHAR(10),
    operation_timestamp VARCHAR(30)
);
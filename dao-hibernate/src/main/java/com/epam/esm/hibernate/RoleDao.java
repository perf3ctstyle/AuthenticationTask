package com.epam.esm.hibernate;

import com.epam.esm.constant.TagConstants;
import com.epam.esm.entity.Role;
import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleDao implements Dao<Role> {

    @PersistenceContext
    private final EntityManager entityManager;

    private static final String GET_ALL = "SELECT ID, NAME FROM TAG LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_BY_NAME = "SELECT ID, NAME FROM ROLE WHERE NAME = :NAME";
    private static final String LIMIT = "LIMIT";
    private static final String OFFSET = "OFFSET";

    @Autowired
    public RoleDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Role> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, Role.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    public Optional<Role> getByName(String name) {
        List<Role> results = entityManager
                .createNativeQuery(GET_BY_NAME, Role.class)
                .setParameter(TagConstants.NAME, name)
                .getResultList();
        return returnSingleFoundResult(results);
    }

    @Override
    public Optional<Role> getById(long id) {
        Role result = entityManager.find(Role.class, id);
        return Optional.ofNullable(result);
    }

    @Override
    public long create(Role role) {
        entityManager.persist(role);
        return role.getId();
    }

    @Override
    public void delete(Role role) {
        entityManager.remove(role);
    }
}

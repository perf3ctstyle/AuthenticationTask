package com.epam.esm.hibernate;

import com.epam.esm.constant.TagConstants;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * This is a class that represents the persistence layer of API and also the Data Access Object(DAO) pattern.
 * It provides basic operations for manipulations with {@link Tag} entities in a database.
 *
 * @author Nikita Torop
 */
@Repository
public class TagDao implements Dao<Tag> {

    @PersistenceContext
    private final EntityManager entityManager;

    private static final String GET_ALL = "SELECT ID, NAME FROM TAG LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_BY_NAME = "SELECT ID, NAME FROM TAG WHERE NAME = :NAME";
    private static final String GET_TAGS_WITH_HIGHEST_COST_OF_ORDERS = "WITH TAG_WITH_COST (id, name, cost_sum) AS "
            + "(SELECT t.id, t.name, SUM(cost) cost_sum FROM tag t "
            + " INNER JOIN gift_and_tag gt on t.id = gt.tag_id "
            + " INNER JOIN user_order uo on gt.certificate_id = uo.gift_certificate_id "
            + " WHERE uo.user_id = :USER_ID "
            + " GROUP BY t.id)"
            + " SELECT id, name FROM TAG_WITH_COST WHERE cost_sum = (SELECT MAX(cost_sum) FROM TAG_WITH_COST)";

    private static final String LIMIT = "LIMIT";
    private static final String OFFSET = "OFFSET";
    private static final String USER_ID = "USER_ID";

    @Autowired
    public TagDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns {@link Tag} objects from a database without any filtering.
     *
     * @param limit  - a number of {@link Tag} objects to return
     * @param offset - a number of {@link Tag} objects to skip when returning
     * @return a {@link List} of {@link Tag} objects.
     */
    public List<Tag> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, Tag.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    /**
     * Returns a {@link Tag} object from a database by its id or throws
     * {@link javax.persistence.NonUniqueResultException} in the case of unexpected behaviour.
     *
     * @param id - the {@link Tag} object's id that is to be retrieved from a database.
     * @return {@link Optional} with a {@link Tag} object if it was found in a database.
     */
    public Optional<Tag> getById(long id) {
        Tag result = entityManager.find(Tag.class, id);
        return Optional.ofNullable(result);
    }

    /**
     * Returns a {@link Tag} object from a database by its name or throws
     * {@link DaoException} in the case of unexpected behaviour.
     *
     * @param name - the {@link Tag} object's name that is to be retrieved from a database.
     * @return {@link Optional} with a {@link Tag} object if it was found in a database.
     */
    public Optional<Tag> getByName(String name) {
        List<Tag> results = entityManager
                .createNativeQuery(GET_BY_NAME, Tag.class)
                .setParameter(TagConstants.NAME, name)
                .getResultList();
        return returnSingleFoundResult(results);
    }

    /**
     * Returns the most widely used tags of {@link com.epam.esm.entity.User} with the highest cost of {@link com.epam.esm.entity.UserOrder}.
     *
     * @return {@link List} of {@link Tag} objects.
     */
    public List<Tag> getMostWidelyUsedTagsOfUserWithHighestCostOfOrders(long userId) {
        return entityManager
                .createNativeQuery(GET_TAGS_WITH_HIGHEST_COST_OF_ORDERS, Tag.class)
                .setParameter(USER_ID, userId)
                .getResultList();
    }

    /**
     * Creates a {@link Tag} object in a database.
     *
     * @param tag - the {@link Tag} object that is to be created in a database.
     * @return {@link Tag} object's id which was created in a database.
     */
    public long create(Tag tag) {
        entityManager.persist(tag);
        return tag.getId();
    }

    /**
     * Deletes a {@link Tag} object in a database by its id.
     *
     * @param tag - the {@link Tag} object that is to be deleted in a database.
     */
    public void delete(Tag tag) {
        entityManager.remove(tag);
    }
}
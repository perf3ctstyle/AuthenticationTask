package com.epam.esm.hibernate;

import com.epam.esm.constant.UserOrderConstants;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.util.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * This is a class that represents the persistence layer of API and also the Data Access Object(DAO) pattern.
 * It provides basic operations for manipulations with {@link UserOrder} entities in a database.
 *
 * @author Nikita Torop
 */
@Repository
public class UserOrderDao implements Dao<UserOrder> {

    @PersistenceContext
    private final EntityManager entityManager;

    private static final String GET_ALL = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_ALL_BY_USER_ID = "SELECT ID, USER_ID, GIFT_CERTIFICATE_ID, COST, PURCHASE_TIMESTAMP FROM USER_ORDER WHERE USER_ID = :USER_ID LIMIT :LIMIT OFFSET :OFFSET";
    private static final String GET_ID_OF_USERS_WITH_HIGHEST_COST_OF_ORDERS = "WITH USER_WITH_COST_OF_ORDERS (user_id, cost_sum) AS " +
            "(SELECT user_id, SUM(COST) as cost_sum FROM user_order GROUP BY user_id) " +
            "SELECT user_id FROM USER_WITH_COST_OF_ORDERS WHERE cost_sum = " +
            "(SELECT MAX(cost_sum) FROM USER_WITH_COST_OF_ORDERS)";

    private static final String LIMIT = "LIMIT";
    private static final String OFFSET = "OFFSET";

    @Autowired
    public UserOrderDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns {@link UserOrder} objects from a database without any filtering.
     *
     * @param limit  - a number of {@link UserOrder} objects to return
     * @param offset - a number of {@link UserOrder} objects to skip when returning
     * @return a {@link List} of {@link UserOrder} objects.
     */
    public List<UserOrder> getAll(int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL, UserOrder.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .getResultList();
    }

    /**
     * Returns {@link UserOrder} objects from a database without any filtering of a particular {@link com.epam.esm.entity.User}.
     *
     * @param userId - {@link com.epam.esm.entity.User} id
     * @param limit  - a number of {@link UserOrder} objects to return
     * @param offset - a number of {@link UserOrder} objects to skip when returning
     * @return a {@link List} of {@link UserOrder} objects.
     */
    public List<UserOrder> getAllByUserId(long userId, int limit, int offset) {
        return entityManager
                .createNativeQuery(GET_ALL_BY_USER_ID, UserOrder.class)
                .setParameter(LIMIT, limit)
                .setParameter(OFFSET, offset)
                .setParameter(UserOrderConstants.USER_ID, userId)
                .getResultList();
    }

    /**
     * Returns a {@link UserOrder} object from a database by its id or throws
     * {@link javax.persistence.NonUniqueResultException} in the case of unexpected behaviour.
     *
     * @param id - the {@link UserOrder} object's id that is to be retrieved from a database.
     * @return {@link Optional} with a {@link UserOrder} object if it was found in a database.
     */
    public Optional<UserOrder> getById(long id) {
        UserOrder result = entityManager.find(UserOrder.class, id);
        return Optional.ofNullable(result);
    }

    /**
     * Creates a {@link UserOrder} object in a database.
     *
     * @param userOrder - the {@link UserOrder} object that is to be created in a database.
     * @return {@link UserOrder} object's id which was created in a database.
     */
    public long create(UserOrder userOrder) {
        entityManager.persist(userOrder);
        return userOrder.getId();
    }

    /**
     * Deletes a {@link UserOrder} object in a database by its id.
     *
     * @param userOrder - the {@link UserOrder} object that is to be deleted in a database.
     */
    public void delete(UserOrder userOrder) {
        entityManager.remove(userOrder);
    }

    public List<Long> getIdsOfUsersWithHighestCostOfOrders() {
        List<BigInteger> ids = entityManager
                .createNativeQuery(GET_ID_OF_USERS_WITH_HIGHEST_COST_OF_ORDERS)
                .getResultList();

        return SqlUtils.toLong(ids);
    }
}

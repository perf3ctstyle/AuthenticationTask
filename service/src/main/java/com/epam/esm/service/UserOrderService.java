package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceAlreadyExistsException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.UserOrderDao;
import com.epam.esm.util.DateTimeUtils;
import com.epam.esm.validator.UserOrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a class that encapsulates the {@link UserOrder} business logic and also acts as a transaction boundary.
 *
 * @author Nikita Torop
 */
@Service
@Transactional
public class UserOrderService implements com.epam.esm.service.Service<UserOrder> {

    private final UserOrderDao userOrderDao;
    private final UserOrderValidator userOrderValidator;
    private final UserService userService;
    private final GiftCertificateService giftCertificateService;

    private static final String USER_ORDER = "User Order";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Autowired
    public UserOrderService(UserOrderDao userOrderDao,
                            UserOrderValidator userOrderValidator,
                            UserService userService,
                            GiftCertificateService giftCertificateService) {
        this.userOrderDao = userOrderDao;
        this.userOrderValidator = userOrderValidator;
        this.userService = userService;
        this.giftCertificateService = giftCertificateService;
    }

    /**
     * Returns {@link UserOrder} objects from a database without any filtering.
     *
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link UserOrder} objects on a page.
     * @return a {@link List} of {@link UserOrder} objects.
     */
    public List<UserOrder> getAll(int page, int pageSize) {
        checkPaginationParameters(page, pageSize);
        return userOrderDao.getAll(pageSize, (page-1)*pageSize);
    }

    /**
     * Returns {@link UserOrder} objects from a database without any filtering of a particular {@link com.epam.esm.entity.User}.
     *
     * @param userId - {@link com.epam.esm.entity.User} id
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link UserOrder} objects on a page.
     * @return a {@link List} of {@link UserOrder} objects.
     */
    public List<UserOrder> getAllByUserId(long userId, int page, int pageSize) {
        checkPaginationParameters(page, pageSize);
        return userOrderDao.getAllByUserId(userId, pageSize, (page-1)*pageSize);
    }

    /**
     * Returns a {@link UserOrder} object from a database by its id or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link UserOrder} object's id that is to be retrieved from a database.
     * @return {@link UserOrder} object.
     */
    public UserOrder getById(long id) {
        Optional<UserOrder> optionalOrder = userOrderDao.getById(id);
        return optionalOrder.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER_ORDER));
    }

    /**
     * Creates a {@link UserOrder} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or {@link ResourceAlreadyExistsException} if the UserOrder with the same name already exists.
     *
     * @param userOrder - the {@link UserOrder} object that is to be created in a database.
     * @return {@link UserOrder} object's id which was created in a database.
     */
    public long create(UserOrder userOrder) {
        userOrderValidator.validateForCreation(userOrder);
        userService.getById(userOrder.getUserId()); // check that user exists
        GiftCertificate giftCertificate = giftCertificateService.getById(userOrder.getGiftCertificateId());

        Integer userOrderCost = giftCertificate.getPrice();
        userOrder.setCost(userOrderCost);

        LocalDateTime purchaseTimestamp = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        userOrder.setPurchaseTimestamp(purchaseTimestamp);

        return userOrderDao.create(userOrder);
    }

    /**
     * Deletes a {@link UserOrder} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link UserOrder} object's id that is to be deleted in a database.
     */
    public void delete(long id) {
        Optional<UserOrder> optionalUserOrder = userOrderDao.getById(id);
        UserOrder userOrder = optionalUserOrder.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER_ORDER));

        userOrderDao.delete(userOrder);
    }
}

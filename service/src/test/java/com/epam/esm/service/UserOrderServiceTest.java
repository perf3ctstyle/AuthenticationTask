package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.UserOrderDao;
import com.epam.esm.validator.UserOrderValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserOrderServiceTest {

    private final UserOrderDao userOrderDao = Mockito.mock(UserOrderDao.class);
    private final UserOrderValidator userOrderValidator = Mockito.mock(UserOrderValidator.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final GiftCertificateService giftCertificateService = Mockito.mock(GiftCertificateService.class);
    private final UserOrderService userOrderService = new UserOrderService(
            userOrderDao,
            userOrderValidator,
            userService,
            giftCertificateService
    );

    private final List<UserOrder> userOrders = new ArrayList<>();
    private final long userOrderId = 1L;
    private final UserOrder userOrder = new UserOrder(userOrderId, 1L, 10, LocalDateTime.MIN);
    private final Optional<UserOrder> optionalUserOrder = Optional.of(userOrder);

    @Test
    public void testShouldGetAllUserOrdersWhenPaginationParametersAreAccurate() {
        int limit = 3;
        int offset = 2;
        Mockito.when(userOrderDao.getAll(limit, offset)).thenReturn(userOrders);

        List<UserOrder> actual = userOrderService.getAll(limit, offset);

        Assertions.assertEquals(userOrders, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenPaginationParametersAreNegativeOnGetAllUsers() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            int limit = -1;
            int offset = -5;

            userOrderService.getAll(limit, offset);
        });
    }

    @Test
    public void testShouldGetAllUserOrdersByUserIdWhenPaginationParametersAreAccurate() {
        long userId = 1L;
        int limit = 5;
        int offset = 7;
        Mockito.when(userOrderDao.getAllByUserId(userId, limit, offset)).thenReturn(userOrders);

        List<UserOrder> actual = userOrderService.getAllByUserId(userId, limit, offset);

        Assertions.assertEquals(userOrders, actual);
    }

    @Test
    public void testShouldGetUserOrderById() {
        Mockito.when(userOrderDao.getById(userOrderId)).thenReturn(optionalUserOrder);

        UserOrder actual = userOrderService.getById(userOrderId);

        Assertions.assertEquals(userOrder, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenUserOrderWasNotFoundById() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(userOrderDao.getById(userOrderId)).thenReturn(Optional.empty());

            userOrderService.getById(userOrderId);
        });
    }

    @Test
    public void testShouldReturnUserOrderIdWhenCreateUserOrder() {
        Mockito.when(userOrderDao.create(userOrder)).thenReturn(userOrderId);
        Mockito.doNothing().when(userOrderValidator).validateForCreation(userOrder);
        Mockito.when(userService.getById(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(giftCertificateService.getById(Mockito.anyLong())).thenReturn(new GiftCertificate());
        Mockito.when(userOrderDao.create(userOrder)).thenReturn(userOrderId);

        long actualUserOrderId = userOrderService.create(userOrder);

        Assertions.assertEquals(userOrderId, actualUserOrderId);
    }

    @Test
    public void testShouldDoNothingWhenDeleteExistingUserOrder() {
        Mockito.when(userOrderDao.getById(userOrderId)).thenReturn(optionalUserOrder);
        Mockito.doNothing().when(userOrderDao).delete(userOrder);

        userOrderService.delete(userOrderId);
    }

    @Test
    public void testShouldThrowExceptionWhenDeleteNonExistentUserOrder() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(userOrderDao.getById(userOrderId)).thenReturn(Optional.empty());

            userOrderService.delete(userOrderId);
        });
    }
}

package com.epam.esm.service;

import com.epam.esm.entity.User;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserServiceTest {

    private final UserDao userDao = Mockito.mock(UserDao.class);
    private final UserService userService = new UserService(userDao);

    private final List<User> users = new ArrayList<>();
    private final User user = new User();
    private final long userId = 1L;
    private final Optional<User> optionalUser = Optional.of(user);

    @Test
    public void testShouldGetAllUsersWhenPaginationParametersAreAccurate() {
        int limit = 3;
        int offset = 2;
        Mockito.when(userDao.getAll(limit, offset)).thenReturn(users);

        List<User> actual = userService.getAll(limit, offset);

        Assertions.assertEquals(users, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenPaginationParametersAreNegativeOnGetAllUsers() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            int limit = -1;
            int offset = -5;

            userService.getAll(limit, offset);
        });
    }

    @Test
    public void testShouldGetUserById() {
        Mockito.when(userDao.getById(userId)).thenReturn(optionalUser);

        User actual = userService.getById(userId);

        Assertions.assertEquals(user, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenUserWasNotFoundById() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(userDao.getById(userId)).thenReturn(Optional.empty());

            userService.getById(userId);
        });
    }

    @Test
    public void testShouldReturnUserIdWhenCreateUser() {
        Mockito.when(userDao.create(user)).thenReturn(userId);

        long actualUserId = userService.create(user);

        Assertions.assertEquals(userId, actualUserId);
    }

    @Test
    public void testShouldDoNothingWhenDeleteUser() {
        Mockito.when(userDao.getById(userId)).thenReturn(optionalUser);
        Mockito.doNothing().when(userDao).delete(user);

        userService.delete(userId);
    }

    @Test
    public void testShouldThrowExceptionWhenDeleteNonExistentUser() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(userDao.getById(userId)).thenReturn(Optional.empty());

            userService.delete(userId);
        });
    }
}

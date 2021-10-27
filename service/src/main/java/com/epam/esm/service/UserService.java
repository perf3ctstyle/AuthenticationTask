package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceAlreadyExistsException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.RoleDao;
import com.epam.esm.hibernate.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This is a class that encapsulates the {@link User} business logic and also acts as a transaction boundary.
 *
 * @author Nikita Torop
 */
@Service
@Transactional
public class UserService implements com.epam.esm.service.Service<User> {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String USER = "User";
    private static final String ROLE = "Role";
    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    public UserService(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns {@link User} objects from a database without any filtering.
     *
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link User} objects on a page.
     * @return a {@link List} of {@link User} objects.
     */
    public List<User> getAll(int page, int pageSize) {
        checkPaginationParameters(page, pageSize);
        return userDao.getAll(pageSize, (page-1)*pageSize);
    }

    /**
     * Returns a {@link User} object from a database by its id or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link User} object's id that is to be retrieved from a database.
     * @return {@link User} object.
     */
    public User getById(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        return optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));
    }

    /**
     * Returns a {@link User} object from a database by its login or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param login - the {@link User} object's login that is to be retrieved from a database.
     * @return {@link User} object.
     */
    public User getByLogin(String login) {
        Optional<User> optionalUser = userDao.getByLogin(login);
        return optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));
    }

    /**
     * Creates a {@link User} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or {@link ResourceAlreadyExistsException} if the User with the same name already exists.
     *
     * @param user - the {@link User} object that is to be created in a database.
     * @return {@link User} object's id which was created in a database.
     */
    public long create(User user) {
        Optional<Role> optionalRoleUser = roleDao.getByName(ROLE_USER);
        Role roleUser = optionalRoleUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, ROLE));
        List<Role> userRoles = Collections.singletonList(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);

        return userDao.create(user);
    }

    /**
     * Deletes a {@link User} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link User} object's id that is to be deleted in a database.
     */
    public void delete(long id) {
        Optional<User> optionalUser = userDao.getById(id);
        User user = optionalUser.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, USER));

        userDao.delete(user);
    }
}

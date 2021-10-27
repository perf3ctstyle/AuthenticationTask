package com.epam.esm.controller;

import com.epam.esm.constant.MessageSourceConstants;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.UserOrderService;
import com.epam.esm.util.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This is a class that represents an API and provides basic operations for manipulations with {@link UserOrder} entities.
 *
 * @author Nikita Torop
 */
@RestController
@RequestMapping("/userOrder")
public class UserOrderController {

    private final UserOrderService userOrderService;
    private final MessageSource messageSource;

    private static final String ID = "id";
    private static final String ID_PATH = "/{id}";

    private static final String JSON = "application/json";

    private static final int BAD_USER_ORDER_RECEIVED_CODE = 40004;
    private static final int USER_ORDER_NOT_FOUND_CODE = 40404;
    private static final int DAO_EXCEPTION_CODE = 50004;

    @Autowired
    public UserOrderController(UserOrderService userOrderService, MessageSource messageSource) {
        this.userOrderService = userOrderService;
        this.messageSource = messageSource;
    }

    /**
     * Returns a {@link List} of {@link UserOrder} objects from a database.
     *
     * @param userId - the {@link User} id whose {@link UserOrder} objects will be searched for.
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link UserOrder} objects on a page.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link List} of {@link User} objects.
     */
    @GetMapping(produces = JSON)
    public ResponseEntity<?> getUserOrders(@RequestParam(required = false) Long userId,
                                           @RequestParam int page,
                                           @RequestParam int pageSize) {
        List<UserOrder> userOrders = (userId == null)
                ? userOrderService.getAll(page, pageSize)
                : userOrderService.getAllByUserId(userId, page, pageSize);

        for (UserOrder userOrder : userOrders) {
            updateGenericUserOrderLinks(userOrder);
        }

        Link link = linkTo(UserOrderController.class).withSelfRel();
        CollectionModel<UserOrder> result = CollectionModel.of(userOrders, link);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Returns a {@link UserOrder} object from a database by its id or throws {@link ResourceNotFoundException}
     * if nothing is retrieved from a database or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link UserOrder} object's id that is to be retrieved from a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link UserOrder} object or a {@link ErrorInfo} object.
     */
    @GetMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> getById(@PathVariable(ID) long id) {
        UserOrder userOrder = userOrderService.getById(id);
        updateGenericUserOrderLinks(userOrder);

        return new ResponseEntity<>(userOrder, HttpStatus.OK);
    }

    /**
     * Creates a {@link UserOrder} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing.
     *
     * @param userOrder - the {@link UserOrder} object that is to be created in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @PostMapping(produces = JSON)
    public ResponseEntity<?> create(@RequestBody UserOrder userOrder) {
        userOrderService.create(userOrder);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Deletes a {@link UserOrder} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link UserOrder} object's id that is to be deleted in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @DeleteMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> deleteById(@PathVariable(ID) long id) {
        userOrderService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(RequiredFieldMissingException.class)
    public ResponseEntity<ErrorInfo> handleRequiredFieldsMissingException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.REQUIRED_FIELDS_MISSING, null, locale),
                BAD_USER_ORDER_RECEIVED_CODE,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.RESOURCE_NOT_FOUND, null, locale),
                USER_ORDER_NOT_FOUND_CODE,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorInfo> handleDaoException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.INTERNAL_ERROR, null, locale),
                DAO_EXCEPTION_CODE,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void updateGenericUserOrderLinks(UserOrder userOrder) {
        userOrder.removeLinks();
        Link selfLink = linkTo(methodOn(UserOrderController.class).getById(userOrder.getId())).withSelfRel();
        userOrder.add(selfLink);
    }
}

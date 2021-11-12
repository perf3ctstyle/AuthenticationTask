package com.epam.esm.controller;


import com.epam.esm.constant.MessageSourceConstants;
import com.epam.esm.dto.AuthenticationRequestDto;
import com.epam.esm.dto.AuthenticationResponseDto;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.security.JwtTokenProvider;
import com.epam.esm.service.UserService;
import com.epam.esm.util.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This is a class that represents an API and provides basic operations for manipulations with {@link User} entities.
 *
 * @author Nikita Torop
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MessageSource messageSource;

    private static final String ID_PATH = "/{id}";
    private static final String LOGIN_PATH = "/login";

    private static final String JSON = "application/json";

    private static final int USER_NOT_FOUND_CODE = 40403;

    @Autowired
    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          MessageSource messageSource) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.messageSource = messageSource;
    }

    @PostMapping(value = LOGIN_PATH, produces = JSON)
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto requestDto) {
        String login = requestDto.getLogin();
        String password = requestDto.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));

        User user = userService.getByLogin(login);
        String token = jwtTokenProvider.createToken(user);

        AuthenticationResponseDto responseDto = new AuthenticationResponseDto(login, token);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * Returns all {@link User} objects from a database.
     *
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link User} objects on a page.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link List} of {@link User} objects.
     */
    @GetMapping(produces = JSON)
    public ResponseEntity<?> getAll(@RequestParam int page, @RequestParam int pageSize) {
        List<User> users = userService.getAll(page, pageSize);

        for (User user : users) {
            updateGenericUserLinks(user);
        }

        Link link = linkTo(UserController.class).withSelfRel();
        CollectionModel<User> result = CollectionModel.of(users, link);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Returns a {@link User} object from a database by its id or throws {@link ResourceNotFoundException}
     * if nothing is retrieved from a database or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link User} object's id that is to be retrieved from a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link User} object or a {@link ErrorInfo} object.
     */
    @GetMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> getById(@PathVariable long id) {
        User user = userService.getById(id);
        updateGenericUserLinks(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Creates a {@link User} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing.
     *
     * @param user - the {@link User} object that is to be created in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @PostMapping(produces = JSON)
    public ResponseEntity<?> create(@RequestBody User user) {
        userService.create(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Deletes a {@link User} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link User} object's id that is to be deleted in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @DeleteMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.RESOURCE_NOT_FOUND, null, locale),
                USER_NOT_FOUND_CODE,
                HttpStatus.NOT_FOUND);
    }

    private void updateGenericUserLinks(User user) {
        user.removeLinks();
        Link selfLink = linkTo(methodOn(UserController.class).getById(user.getId())).withSelfRel();
        user.add(selfLink);
    }
}

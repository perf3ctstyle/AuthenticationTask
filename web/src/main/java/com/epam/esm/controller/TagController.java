package com.epam.esm.controller;

import com.epam.esm.constant.MessageSourceConstants;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceAlreadyExistsException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.TagService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This is a class that represents an API and provides basic operations for manipulations with {@link Tag} entities.
 *
 * @author Nikita Torop
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;
    private final MessageSource messageSource;

    private static final String ID = "id";
    private static final String USER_ID = "userId";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String ID_PATH = "/{id}";

    private static final String JSON = "application/json";

    private static final int BAD_TAG_RECEIVED_CODE = 40002;
    private static final int TAG_ALREADY_EXISTS_CODE = 40902;
    private static final int TAG_NOT_FOUND_CODE = 40402;
    private static final int DAO_EXCEPTION_CODE = 50002;

    @Autowired
    public TagController(TagService tagService, MessageSource messageSource) {
        this.tagService = tagService;
        this.messageSource = messageSource;
    }

    /**
     * Returns {@link Tag} objects from a database without any filtering.
     *
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link Tag} objects on a page.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link List} of {@link Tag} objects.
     */
    @GetMapping(params = {LIMIT, OFFSET}, produces = JSON)
    public ResponseEntity<?> getAll(int page, int pageSize) {
        List<Tag> tags = tagService.getAll(page, pageSize);
        CollectionModel<Tag> result = updateGenericTagLinks(tags);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Returns a {@link Tag} object from a database by its id or throws {@link ResourceNotFoundException}
     * if nothing is retrieved from a database or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link Tag} object's id that is to be retrieved from a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link Tag} object or a {@link ErrorInfo} object.
     */
    @GetMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> getById(@PathVariable(ID) long id) {
        Tag tag = tagService.getById(id);
        updateGenericTagLinks(tag);

        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    /**
     * Returns the most widely used tags of {@link com.epam.esm.entity.User} with the highest cost of {@link com.epam.esm.entity.UserOrder}.
     *
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link Set} of most widely used
     * {@link Tag} objects or a {@link ErrorInfo} object.
     */
    @GetMapping(produces = JSON)
    public ResponseEntity<?> getMostWidelyUsedTagsOfUsersWithHighestCostOfOrders() {
        Set<Tag> mostWidelyUsedTagsOfUsersWithHighestCostOfOrders
                = tagService.getMostWidelyUsedTagsOfUsersWithHighestCostOfOrders();
        CollectionModel<Tag> result = updateGenericTagLinks(mostWidelyUsedTagsOfUsersWithHighestCostOfOrders);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Creates a {@link Tag} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or {@link ResourceAlreadyExistsException} if the tag with the same name already exists.
     *
     * @param tag - the {@link Tag} object that is to be created in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @PostMapping(produces = JSON)
    public ResponseEntity<?> create(@RequestBody Tag tag) {
        tagService.create(tag);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Deletes a {@link Tag} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link Tag} object's id that is to be deleted in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @DeleteMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> deleteById(@PathVariable(ID) long id) {
        tagService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorInfo> handleIllegalArgumentException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.ILLEGAL_ARGUMENT, null, locale),
                BAD_TAG_RECEIVED_CODE,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequiredFieldMissingException.class)
    public ResponseEntity<ErrorInfo> handleRequiredFieldsMissingException(RequiredFieldMissingException exception, Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.REQUIRED_FIELDS_MISSING, null, locale) + exception.getFieldName(),
                BAD_TAG_RECEIVED_CODE,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorInfo> handleResourceAlreadyExistsException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.RESOURCE_ALREADY_EXISTS, null, locale),
                TAG_ALREADY_EXISTS_CODE,
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(ResourceNotFoundException exception, Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.RESOURCE_NOT_FOUND, null, locale) + exception.getResourceName(),
                TAG_NOT_FOUND_CODE,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorInfo> handleDaoException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.INTERNAL_ERROR, null, locale),
                DAO_EXCEPTION_CODE,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void updateGenericTagLinks(Tag tag) {
        tag.removeLinks();
        Link selfLink = linkTo(methodOn(TagController.class).getById(tag.getId())).withSelfRel();
        tag.add(selfLink);
    }

    private CollectionModel<Tag> updateGenericTagLinks(Collection<Tag> tags) {
        for (Tag tag : tags) {
            updateGenericTagLinks(tag);
        }

        Link link = linkTo(TagController.class).withSelfRel();
        return CollectionModel.of(tags, link);
    }
}

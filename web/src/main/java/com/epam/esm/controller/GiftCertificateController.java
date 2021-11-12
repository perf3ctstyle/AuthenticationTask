package com.epam.esm.controller;

import com.epam.esm.constant.MessageSourceConstants;
import com.epam.esm.dto.SearchInfoDto;
import com.epam.esm.entity.ErrorInfo;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.GiftCertificateService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This is a class that represents the presentation layer of API and provides basic operations for manipulations with {@link GiftCertificate} entities.
 *
 * @author Nikita Torop
 */
@RestController
@RequestMapping(value = "/gift")
public class GiftCertificateController {

    private final GiftCertificateService giftCertificateService;
    private final MessageSource messageSource;

    private static final String ID = "id";
    private static final String ID_PATH = "/{id}";

    private static final String JSON = "application/json";

    private static final int BAD_CERTIFICATE_RECEIVED_CODE = 40001;
    private static final int CERTIFICATE_NOT_FOUND_CODE = 40401;
    private static final int DAO_EXCEPTION_CODE = 50001;

    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService, MessageSource messageSource) {
        this.giftCertificateService = giftCertificateService;
        this.messageSource = messageSource;
    }

    /**
     * Returns a {@link List} of {@link GiftCertificate} objects from a database.
     *
     * @param searchInfo - an object that contains the partial search and sorting info
     * @param tagNames   - names of {@link Tag} objects which are linked to a {@link GiftCertificate} object.
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link GiftCertificate} objects on a page.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link List} of {@link GiftCertificate} objects or a {@link ErrorInfo} object.
     */
    @GetMapping(produces = JSON)
    public ResponseEntity<?> getGiftCertificates(@RequestBody(required = false) SearchInfoDto searchInfo,
                                                 @RequestParam(required = false) String[] tagNames,
                                                 @RequestParam int page,
                                                 @RequestParam int pageSize) {
        List<GiftCertificate> giftCertificates = (searchInfo == null)
                ? giftCertificateService.getAll(page, pageSize)
                : giftCertificateService.getGiftCertificates(searchInfo, tagNames, page, pageSize);

        for (GiftCertificate giftCertificate : giftCertificates) {
            updateGenericGiftCertificateLinks(giftCertificate);
        }

        Link link = linkTo(GiftCertificateController.class).withSelfRel();
        CollectionModel<GiftCertificate> result = CollectionModel.of(giftCertificates, link);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Returns a {@link GiftCertificate} object from a database by its id or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link GiftCertificate} object's id that is to be retrieved from a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} and a {@link GiftCertificate} object or a {@link ErrorInfo} object.
     */
    @GetMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> getById(@PathVariable(ID) long id) {
        GiftCertificate giftCertificate = giftCertificateService.getById(id);
        updateGenericGiftCertificateLinks(giftCertificate);

        return new ResponseEntity<>(giftCertificate, HttpStatus.OK);
    }

    /**
     * Creates a {@link GiftCertificate} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or throws {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0.
     *
     * @param giftCertificate - the {@link GiftCertificate} object that is to be created in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @PostMapping(produces = JSON)
    public ResponseEntity<?> create(@RequestBody GiftCertificate giftCertificate) {
        giftCertificateService.create(giftCertificate);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Updates a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist or throws {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0
     * or if such field of {@link GiftCertificate} doesn't exist.
     *
     * @param id                     - the {@link GiftCertificate} object's id that is to be updated in a database.
     * @param fieldAndValueForUpdate - the new values for updating a {@link GiftCertificate} object in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @PatchMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> updateById(@PathVariable long id, @RequestBody Map<String, String> fieldAndValueForUpdate) {
        giftCertificateService.updateById(id, fieldAndValueForUpdate);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Updates a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist or {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0.
     *
     * @param id              - the {@link GiftCertificate} object's id that is to be updated in a database.
     * @param giftCertificate - the {@link GiftCertificate} object which has the new values for update in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @PutMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> updateById(@PathVariable long id, @RequestBody GiftCertificate giftCertificate) {
        giftCertificateService.updateById(id, giftCertificate);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link GiftCertificate} object's id that is to be deleted in a database.
     * @return {@link ResponseEntity} with a {@link HttpStatus} alone or additionally with a {@link ErrorInfo} object.
     */
    @DeleteMapping(value = ID_PATH, produces = JSON)
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        giftCertificateService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorInfo> handleIllegalArgumentException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.ILLEGAL_ARGUMENT, null, locale),
                BAD_CERTIFICATE_RECEIVED_CODE,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequiredFieldMissingException.class)
    public ResponseEntity<ErrorInfo> handleRequiredFieldsMissingException(RequiredFieldMissingException exception, Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.REQUIRED_FIELDS_MISSING, null, locale) + exception.getFieldName(),
                BAD_CERTIFICATE_RECEIVED_CODE,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(ResourceNotFoundException exception, Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.RESOURCE_NOT_FOUND, null, locale) + exception.getResourceName(),
                CERTIFICATE_NOT_FOUND_CODE,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorInfo> handleDaoException(Locale locale) {
        return ControllerUtils.createResponseEntityWithSpecifiedErrorInfo(
                messageSource.getMessage(MessageSourceConstants.INTERNAL_ERROR, null, locale),
                DAO_EXCEPTION_CODE,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void updateGenericGiftCertificateLinks(GiftCertificate giftCertificate) {
        giftCertificate.removeLinks();
        Link selfLink = linkTo(methodOn(GiftCertificateController.class).getById(giftCertificate.getId())).withSelfRel();
        giftCertificate.add(selfLink);
    }
}

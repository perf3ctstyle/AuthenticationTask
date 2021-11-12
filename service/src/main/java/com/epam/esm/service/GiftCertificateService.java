package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.dto.SearchInfoDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.GiftCertificateDao;
import com.epam.esm.util.DateTimeUtils;
import com.epam.esm.validator.GiftCertificateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.constant.GenericConstants.DATE_TIME_PATTERN;

/**
 * This is a class that encapsulates the {@link GiftCertificate} business logic and also acts as a transaction boundary.
 *
 * @author Nikita Torop
 */
@Service
@Transactional
public class GiftCertificateService implements com.epam.esm.service.Service<GiftCertificate> {

    private final GiftCertificateDao giftCertificateDao;
    private final GiftCertificateValidator giftCertificateValidator;
    private final TagService tagService;

    private static final String GIFT_CERTIFICATE = "Gift Certificate";

    @Autowired
    public GiftCertificateService(GiftCertificateDao giftCertificateDao,
                                  GiftCertificateValidator giftCertificateValidator,
                                  TagService tagService) {
        this.giftCertificateDao = giftCertificateDao;
        this.giftCertificateValidator = giftCertificateValidator;
        this.tagService = tagService;
    }

    /**
     * Returns {@link GiftCertificate} objects from a database without any filtering.
     *
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link GiftCertificate} objects on a page.
     * @return a {@link List} of {@link GiftCertificate} objects.
     */
    public List<GiftCertificate> getAll(int page, int pageSize) {
        checkPaginationParameters(page, pageSize);
        return giftCertificateDao.getAll(pageSize, (page-1)*pageSize);
    }

    /**
     * Returns a {@link List} of {@link GiftCertificate} objects from a database.
     *
     * @param searchInfo - an object that contains the partial search and sorting info
     * @param tagNames   - names of {@link Tag} objects which are linked to a {@link GiftCertificate} object.
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link GiftCertificate} objects on a page.
     * @return a {@link List} of {@link GiftCertificate} objects.
     */
    public List<GiftCertificate> getGiftCertificates(SearchInfoDto searchInfo, String[] tagNames, int page, int pageSize) {
        checkPaginationParameters(page, pageSize);

        SearchInfoDto searchInfoToUse = (searchInfo != null) ? searchInfo : new SearchInfoDto();
        String[] tagNamesToUse = (tagNames != null) ? tagNames : new String[0];

        return giftCertificateDao.getGiftCertificates(searchInfoToUse, tagNamesToUse, pageSize, (page-1)*pageSize);
    }

    /**
     * Returns a {@link GiftCertificate} object from a database by its id or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link GiftCertificate} object's id that is to be retrieved from a database.
     * @return {@link GiftCertificate} object.
     */
    public GiftCertificate getById(long id) {
        Optional<GiftCertificate> optionalGiftCertificate = giftCertificateDao.getById(id);
        return optionalGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));
    }

    /**
     * Creates a {@link GiftCertificate} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0.
     *
     * @param giftCertificate - the {@link GiftCertificate} object that is to be created in a database.
     * @return {@link GiftCertificate} object's id which was created in a database.
     */
    public long create(GiftCertificate giftCertificate) {
        giftCertificateValidator.validateForCreation(giftCertificate);

        LocalDateTime currentDateTime = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        giftCertificate.setCreateDate(currentDateTime);
        giftCertificate.setLastUpdateDate(currentDateTime);

        persistGiftCertificateTagsIfExist(giftCertificate);

        return giftCertificateDao.create(giftCertificate);
    }

    /**
     * Updates a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist or {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0.
     *
     * @param id                     - the {@link GiftCertificate} object's id that is to be updated in a database.
     * @param fieldAndValueForUpdate - the new values for updating a {@link GiftCertificate} object in a database.
     */
    public void updateById(long id, Map<String, String> fieldAndValueForUpdate) {
        Optional<GiftCertificate> oldGiftCertificate = giftCertificateDao.getById(id);
        oldGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        giftCertificateValidator.validateForUpdate(fieldAndValueForUpdate);

        GiftCertificate newGiftCertificate = new GiftCertificate(oldGiftCertificate.get());
        newGiftCertificate.updateValuesFromMap(fieldAndValueForUpdate);

        LocalDateTime lastUpdateDate = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        newGiftCertificate.setLastUpdateDate(lastUpdateDate);

        giftCertificateDao.update(newGiftCertificate);
    }

    /**
     * Updates a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist or {@link IllegalArgumentException} if the parameter object's price or duration values are lower than 0.
     *
     * @param id              - the {@link GiftCertificate} object's id that is to be updated in a database.
     * @param giftCertificate - the {@link GiftCertificate} object which has the new values for update in a database.
     */
    public void updateById(long id, GiftCertificate giftCertificate) {
        Optional<GiftCertificate> oldGiftCertificate = giftCertificateDao.getById(id);
        oldGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        giftCertificateValidator.validateForUpdate(giftCertificate);

        LocalDateTime lastUpdateDate = DateTimeUtils.nowOfPattern(DATE_TIME_PATTERN);
        giftCertificate.setLastUpdateDate(lastUpdateDate);

        persistGiftCertificateTagsIfExist(giftCertificate);

        giftCertificateDao.update(giftCertificate);
    }

    /**
     * Deletes a {@link GiftCertificate} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link GiftCertificate} object's id that is to be deleted in a database.
     */
    public void delete(long id) {
        Optional<GiftCertificate> optionalGiftCertificate = giftCertificateDao.getById(id);
        GiftCertificate giftCertificate = optionalGiftCertificate.orElseThrow(
                () -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, GIFT_CERTIFICATE));

        giftCertificateDao.delete(giftCertificate);
    }

    private void persistGiftCertificateTagsIfExist(GiftCertificate giftCertificate) {
        List<Tag> notPersistedTags = giftCertificate.getTags();
        List<Tag> persistedTags = tagService.getPersistedTagsIfExist(notPersistedTags);
        giftCertificate.setTags(persistedTags);
    }
}

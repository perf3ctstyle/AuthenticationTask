package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DaoException;
import com.epam.esm.exception.RequiredFieldMissingException;
import com.epam.esm.exception.ResourceAlreadyExistsException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.TagDao;
import com.epam.esm.hibernate.UserOrderDao;
import com.epam.esm.validator.TagValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This is a class that encapsulates the {@link Tag} business logic and also acts as a transaction boundary.
 *
 * @author Nikita Torop
 */
@Service
@Transactional
public class TagService implements com.epam.esm.service.Service<Tag> {

    private final TagDao tagDao;
    private final UserOrderDao userOrderDao;
    private final TagValidator tagValidator;

    private static final String TAG = "Tag";
    private static final String TAG_WITH_NAME_ALREADY_EXISTS = "Unfortunately, a tag with this name already exists.";

    @Autowired
    public TagService(TagDao tagDao, UserOrderDao userOrderDao, TagValidator tagValidator) {
        this.tagDao = tagDao;
        this.userOrderDao = userOrderDao;
        this.tagValidator = tagValidator;
    }

    /**
     * Returns {@link Tag} objects from a database without any filtering.
     *
     * @param page  - the number of a page to show.
     * @param pageSize - the number of {@link Tag} objects on a page.
     * @return a {@link List} of {@link Tag} objects.
     */
    public List<Tag> getAll(int page, int pageSize) {
        checkPaginationParameters(page, pageSize);
        return tagDao.getAll(pageSize, (page-1)*pageSize);
    }

    /**
     * Returns a {@link Tag} object from a database by its id or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param id - the {@link Tag} object's id that is to be retrieved from a database.
     * @return {@link Tag} object.
     */
    public Tag getById(long id) {
        Optional<Tag> optionalTag = tagDao.getById(id);
        return optionalTag.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, TAG));
    }

    /**
     * Returns a {@link Tag} object from a database by its name or throws {@link ResourceNotFoundException} if nothing is retrieved from a database
     * or {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param name - the {@link Tag} object's name that is to be retrieved from a database.
     * @return {@link Tag} object.
     */
    public Tag getByName(String name) {
        Optional<Tag> optionalTag = tagDao.getByName(name);
        return optionalTag.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, TAG));
    }

    /**
     * Returns the most widely used tags of {@link com.epam.esm.entity.User} with the highest cost of {@link com.epam.esm.entity.UserOrder}.
     *
     * @return {@link Set} of {@link Tag} objects.
     */
    public Set<Tag> getMostWidelyUsedTagsOfUsersWithHighestCostOfOrders() {
        List<Long> idsOfUsersWithHighestCostOfOrders = userOrderDao.getIdsOfUsersWithHighestCostOfOrders();
        Set<Tag> mostWidelyUsedTagsOfUsersWithHighestCostOfOrders = new HashSet<>();

        for (Long userId : idsOfUsersWithHighestCostOfOrders) {
            List<Tag> mostWidelyUsedTags = tagDao.getMostWidelyUsedTagsOfUserWithHighestCostOfOrders(userId);
            mostWidelyUsedTagsOfUsersWithHighestCostOfOrders.addAll(mostWidelyUsedTags);
        }

        return mostWidelyUsedTagsOfUsersWithHighestCostOfOrders;
    }

    /**
     * Creates a {@link Tag} object in a database or throws {@link RequiredFieldMissingException} if some fields
     * required for creation are missing or {@link ResourceAlreadyExistsException} if the tag with the same name already exists.
     *
     * @param tag - the {@link Tag} object that is to be created in a database.
     * @return {@link Tag} object's id which was created in a database.
     */
    public long create(Tag tag) {
        tagValidator.validateForCreation(tag);

        Optional<Tag> optionalTag = tagDao.getByName(tag.getName());
        if (optionalTag.isPresent()) {
            throw new ResourceAlreadyExistsException(TAG_WITH_NAME_ALREADY_EXISTS);
        }

        return tagDao.create(tag);
    }

    /**
     * Deletes a {@link Tag} object in a database by its id or throws {@link ResourceNotFoundException} if the object
     * with such id doesn't exist.
     *
     * @param id - the {@link Tag} object's id that is to be deleted in a database.
     */
    public void delete(long id) {
        Optional<Tag> optionalTag = tagDao.getById(id);
        Tag tag = optionalTag.orElseThrow(() -> new ResourceNotFoundException(GenericExceptionMessageConstants.RESOURCE_NOT_FOUND, TAG));

        tagDao.delete(tag);
    }

    /**
     * Returns persisted {@link Tag} objects if they exist in a database or themselves if they do not exist
     * or throws {@link DaoException} in the case of unexpected behaviour on a Dao level.
     *
     * @param tags - the {@link Tag} objects that are to be checked for persistence in a database.
     * @return {@link List} of {@link Tag} objects.
     */
    public List<Tag> getPersistedTagsIfExist(List<Tag> tags) {
        List<Tag> tagsToReturn = new ArrayList<>();

        for (Tag tag : tags) {
            Optional<Tag> optionalTag = tagDao.getByName(tag.getName());
            Tag tagToReturn = optionalTag.orElse(tag);
            tagsToReturn.add(tagToReturn);
        }

        return tagsToReturn;
    }
}

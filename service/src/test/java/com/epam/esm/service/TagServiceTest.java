package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ResourceAlreadyExistsException;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.hibernate.TagDao;
import com.epam.esm.hibernate.UserOrderDao;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TagServiceTest {

    private final TagDao tagDao = Mockito.mock(TagDao.class);
    private final UserOrderDao userOrderDao = Mockito.mock(UserOrderDao.class);
    private final TagValidator tagValidator = Mockito.mock(TagValidator.class);
    private final TagService tagService = new TagService(tagDao, userOrderDao, tagValidator);

    private final List<Tag> tags = new ArrayList<>();
    private final Tag tag = new Tag();
    private final long tagId = 1L;
    private final String name = "tag";
    private final Optional<Tag> optionalTag = Optional.of(tag);

    @Test
    public void testShouldGetAllTagsWhenPaginationParametersAreAccurate() {
        int limit = 3;
        int offset = 2;
        Mockito.when(tagDao.getAll(limit, offset)).thenReturn(tags);

        List<Tag> actual = tagService.getAll(limit, offset);

        Assertions.assertEquals(tags, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenPaginationParametersAreNegativeOnGetAllTags() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            int limit = -1;
            int offset = -5;

            tagService.getAll(limit, offset);
        });
    }

    @Test
    public void testShouldGetTagById() {
        Mockito.when(tagDao.getById(tagId)).thenReturn(optionalTag);

        Tag actual = tagService.getById(tagId);

        Assertions.assertEquals(tag, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenTagWasNotFoundById() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(tagDao.getById(tagId)).thenReturn(Optional.empty());

            tagService.getById(tagId);
        });
    }

    @Test
    public void testShouldGetTagByName() {
        Mockito.when(tagDao.getByName(name)).thenReturn(optionalTag);

        Tag actual = tagService.getByName(name);

        Assertions.assertEquals(tag, actual);
    }

    @Test
    public void testShouldThrowExceptionWhenTagWasNotFoundByName() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(tagDao.getByName(name)).thenReturn(Optional.empty());

            tagService.getByName(name);
        });
    }

    @Test
    public void testShouldGetMostWidelyUsedTagsOfUsersWithHighestCostOfOrders() {
        long userId = 2L;
        Mockito.when(userOrderDao.getIdsOfUsersWithHighestCostOfOrders()).thenReturn(Collections.singletonList(userId));
        Mockito.when(tagDao.getMostWidelyUsedTagsOfUserWithHighestCostOfOrders(userId)).thenReturn(tags);
        Set<Tag> expected = new HashSet<>(tags);

        Set<Tag> actual = tagService.getMostWidelyUsedTagsOfUsersWithHighestCostOfOrders();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testShouldReturnTagIdWhenCreateTag() {
        tag.setName(name);
        Mockito.doNothing().when(tagValidator).validateForCreation(tag);
        Mockito.when(tagDao.getByName(name)).thenReturn(Optional.empty());
        Mockito.when(tagDao.create(tag)).thenReturn(tagId);

        long actualTagId = tagService.create(tag);

        Assertions.assertEquals(tagId, actualTagId);
    }

    @Test
    public void testShouldThrowExceptionWhenTagWithSameNameExistsInCreateTag() {
        Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> {
            tag.setName(name);
            Mockito.when(tagDao.getByName(name)).thenReturn(Optional.of(tag));

            tagService.create(tag);
        });
    }

    @Test
    public void testShouldDoNothingWhenDeleteTag() {
        Mockito.when(tagDao.getById(tagId)).thenReturn(optionalTag);
        Mockito.doNothing().when(tagDao).delete(tag);

        tagService.delete(tagId);
    }

    @Test
    public void testShouldThrowExceptionWhenDeleteNonExistentTag() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Mockito.when(tagDao.getById(tagId)).thenReturn(Optional.empty());

            tagService.delete(tagId);
        });
    }
}

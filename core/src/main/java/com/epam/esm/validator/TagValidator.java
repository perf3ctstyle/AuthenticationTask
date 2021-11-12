package com.epam.esm.validator;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.RequiredFieldMissingException;
import org.apache.commons.lang3.StringUtils;

public class TagValidator {

    private static final String NAME = "name";

    public void validateForCreation(Tag tag) {
        String name = tag.getName();

        if (name == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, NAME);
        }
        if (StringUtils.isWhitespace(name)) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.EMPTY_VALUE);
        }
    }
}
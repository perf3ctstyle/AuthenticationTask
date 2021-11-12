package com.epam.esm.validator;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.constant.GiftCertificateConstants;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.RequiredFieldMissingException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

public class GiftCertificateValidator {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String DURATION = "duration";

    public void validateForCreation(GiftCertificate giftCertificate) {
        String name = giftCertificate.getName();
        String description = giftCertificate.getDescription();
        Integer price = giftCertificate.getPrice();
        Long duration = giftCertificate.getDuration();

        if (name == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, NAME);
        }
        if (description == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, DESCRIPTION);
        }
        if (StringUtils.isWhitespace(name) || StringUtils.isWhitespace(description)) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.EMPTY_VALUE);
        }
        if (price == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, PRICE);
        }
        if (duration == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, DURATION);
        }
        if (price <= 0 || duration <= 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }
    }

    public void validateForUpdate(GiftCertificate giftCertificate) {
        String name = giftCertificate.getName();
        String description = giftCertificate.getDescription();
        Integer price = giftCertificate.getPrice();
        Long duration = giftCertificate.getDuration();

        validateNameAndDescription(name, description);
        validatePriceAndDuration(price, duration);
    }

    public void validateForUpdate(Map<String, String> fieldAndValueForUpdate) {
        Set<String> fieldNames = fieldAndValueForUpdate.keySet();

        for (String fieldName : fieldNames) {
            if (!GiftCertificateConstants.FIELDS.contains(fieldName.toUpperCase())) {
                throw new IllegalArgumentException(fieldName + GenericExceptionMessageConstants.SUCH_FIELD_DOES_NOT_EXIST);
            }
        }

        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.updateValuesFromMap(fieldAndValueForUpdate);

        validateNameAndDescription(giftCertificate.getName(), giftCertificate.getDescription());
        validatePriceAndDuration(giftCertificate.getPrice(), giftCertificate.getDuration());
    }

    private void validateNameAndDescription(String name, String description) {
        if (StringUtils.isWhitespace(name) || StringUtils.isWhitespace(description)) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.EMPTY_VALUE);
        }
    }

    private void validatePriceAndDuration(Integer price, Long duration) {
        if ((price != null && price <= 0) || (duration != null && duration <= 0))  {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }
    }
}
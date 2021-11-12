package com.epam.esm.validator;

import com.epam.esm.constant.GenericExceptionMessageConstants;
import com.epam.esm.entity.UserOrder;
import com.epam.esm.exception.RequiredFieldMissingException;

public class UserOrderValidator {

    private static final String USER_ID = "user id";
    private static final String GIFT_CERTIFICATE_ID = "gift certificate id";

    public void validateForCreation(UserOrder userOrder) {
        if (userOrder.getUserId() == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, USER_ID);
        }
        if (userOrder.getGiftCertificateId() == null) {
            throw new RequiredFieldMissingException(GenericExceptionMessageConstants.REQUIRED_FIELD_MISSING, GIFT_CERTIFICATE_ID);
        }
    }
}

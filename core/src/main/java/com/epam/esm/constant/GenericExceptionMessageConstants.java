package com.epam.esm.constant;

public class GenericExceptionMessageConstants {

    private GenericExceptionMessageConstants() {
    }

    public static final String RESOURCE_NOT_FOUND = "The required resource wasn't found.";
    public static final String SUCH_FIELD_DOES_NOT_EXIST = " field doesn't exist.";
    public static final String REQUIRED_FIELD_MISSING = "The required field was missing.";
    public static final String NEGATIVE_VALUE_PROHIBITED = "Negative value was found in a field that is supposed to be positive.";
    public static final String EMPTY_VALUE = "Some of the fields didn't equal null, but had empty values";
    public static final String MORE_ENTITIES_THAN_EXPECTED = "Expected 1 entity, but received more.";
}

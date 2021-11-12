package com.epam.esm.constant;

import java.util.Arrays;
import java.util.List;

public class GiftCertificateConstants {

    private GiftCertificateConstants() {
    }

    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String PRICE = "PRICE";
    public static final String DURATION = "DURATION";
    public static final String CREATE_DATE = "CREATE_DATE";
    public static final String LAST_UPDATE_DATE = "LAST_UPDATE_DATE";
    public static final List<String> FIELDS = Arrays.asList(ID, NAME, DESCRIPTION, PRICE, DURATION, CREATE_DATE, LAST_UPDATE_DATE);
}

package com.epam.esm.constructor;

public class GiftCertificateQueryConstructor {

    private static final String GET_DISTINCT_CERTIFICATE_ID_WITH_JOIN_TO_TAG = "SELECT DISTINCT certificate_id FROM gift_and_tag gat INNER JOIN tag t ON gat.tag_id = t.id ";
    private static final String GET_CERTIFICATE_ID_BY_TAG_NAME_SUB_QUERY = " (SELECT certificate_id FROM gift_and_tag gat INNER JOIN tag t on gat.tag_id = t.id WHERE t.name = '%s') ";
    private static final String CERTIFICATE_ID_IN = " certificate_id IN ";
    private static final String AND = " AND ";
    private static final String WHERE = " WHERE ";

    private GiftCertificateQueryConstructor() {
    }

    public static String getIdsOfGiftCertificatesWithAndConnectionToTags(String[] tagNames) {
        StringBuilder stringBuilder = new StringBuilder();

        if (tagNames != null && tagNames.length > 0) {
            stringBuilder
                    .append(GET_DISTINCT_CERTIFICATE_ID_WITH_JOIN_TO_TAG)
                    .append(WHERE)
                    .append(CERTIFICATE_ID_IN);

            for (String tagName : tagNames) {
                String subQuery = String.format(GET_CERTIFICATE_ID_BY_TAG_NAME_SUB_QUERY, tagName);
                stringBuilder
                        .append(subQuery)
                        .append(AND)
                        .append(CERTIFICATE_ID_IN);
            }

            int lengthWithoutLastAnd = stringBuilder.length() - (AND.length() + CERTIFICATE_ID_IN.length());
            stringBuilder.setLength(lengthWithoutLastAnd);
        }

        return stringBuilder.toString();
    }
}

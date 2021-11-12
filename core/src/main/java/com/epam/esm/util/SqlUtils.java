package com.epam.esm.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlUtils {

    private static final String AND = " AND ";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String WHERE = " WHERE ";
    private static final String LIKE = " LIKE ";
    private static final String ASCENDING = " ASC";
    private static final String DESCENDING = " DESC";
    private static final String LIMIT = " LIMIT ";
    private static final String OFFSET = " OFFSET ";
    private static final String WILDCARD_ANY = "%";
    private static final String SINGLE_QUOTE = "'";
    private static final String COMMA = ",";
    private static final String EQUAL = " = ";
    private static final String ID = " ID = ";

    private SqlUtils() {
    }

    public static String constructQueryForUpdating(String query, long id, Map<String, String> fieldNameValueForUpdate) {
        StringBuilder stringBuilder = new StringBuilder(query);

        if (fieldNameValueForUpdate != null && !fieldNameValueForUpdate.isEmpty()) {

            for (Map.Entry<String, String> entry : fieldNameValueForUpdate.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldName != null) {
                    stringBuilder
                            .append(fieldName)
                            .append(EQUAL)
                            .append(SINGLE_QUOTE)
                            .append(fieldValue)
                            .append(SINGLE_QUOTE)
                            .append(COMMA);
                }
            }
        }

        int lastInsertedCommaIndex = stringBuilder.length() - 1;
        stringBuilder.deleteCharAt(lastInsertedCommaIndex);

        stringBuilder.append(WHERE).append(ID).append(id);

        return stringBuilder.toString();
    }

    public static String applyPartialSearch(Map<String, String> fieldAndValueForPartialSearch) {
        StringBuilder stringBuilder = new StringBuilder();

        if (fieldAndValueForPartialSearch != null && !fieldAndValueForPartialSearch.isEmpty()) {
            for (Map.Entry<String, String> entry : fieldAndValueForPartialSearch.entrySet()) {
                String fieldName = entry.getKey();
                String partOfField = entry.getValue();

                if (partOfField != null) {
                    stringBuilder
                            .append(fieldName)
                            .append(LIKE)
                            .append(SINGLE_QUOTE)
                            .append(WILDCARD_ANY)
                            .append(partOfField)
                            .append(WILDCARD_ANY)
                            .append(SINGLE_QUOTE)
                            .append(AND);
                }
            }

            int lengthWithoutLastAnd = stringBuilder.length() - AND.length();
            stringBuilder.setLength(lengthWithoutLastAnd);
        }

        return stringBuilder.toString();
    }

    public static String applySorting(String fieldForSorting, Boolean ascending) {
        StringBuilder stringBuilder = new StringBuilder();

        if (StringUtils.isNotBlank(fieldForSorting) && ascending != null) {
            stringBuilder
                    .append(ORDER_BY)
                    .append(fieldForSorting);

            if (ascending) {
                stringBuilder.append(ASCENDING);
            } else {
                stringBuilder.append(DESCENDING);
            }
        }

        return stringBuilder.toString();
    }

    public static String applyPagination(Integer limit, Integer offset) {
        StringBuilder stringBuilder = new StringBuilder();

        if (limit != null) {
            stringBuilder
                    .append(LIMIT)
                    .append(limit);
        }
        if (offset != null) {
            stringBuilder
                    .append(OFFSET)
                    .append(offset);
        }

        return stringBuilder.toString();
    }

    public static List<Long> toLong(List<BigInteger> bigIntegers) {
        List<Long> result = new ArrayList<>();
        for (BigInteger bigInteger : bigIntegers) {
            result.add(bigInteger.longValue());
        }

        return result;
    }
}

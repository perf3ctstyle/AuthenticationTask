package com.epam.esm.dto;

import java.util.Map;

public class SearchInfoDto {

    private Map<String, String> fieldAndValueForPartialSearch;
    private String fieldForSorting;
    private Boolean ascending;

    public SearchInfoDto() {
    }

    public void setFieldAndValueForPartialSearch(Map<String, String> fieldAndValueForPartialSearch) {
        this.fieldAndValueForPartialSearch = fieldAndValueForPartialSearch;
    }

    public Map<String, String> getFieldAndValueForPartialSearch() {
        return fieldAndValueForPartialSearch;
    }

    public void setFieldForSorting(String fieldForSorting) {
        this.fieldForSorting = fieldForSorting;
    }

    public String getFieldForSorting() {
        return fieldForSorting;
    }

    public void setAscending(Boolean ascending) {
        this.ascending = ascending;
    }

    public Boolean isAscending() {
        return ascending;
    }
}

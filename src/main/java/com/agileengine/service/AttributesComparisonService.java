package com.agileengine.service;

public interface AttributesComparisonService {

    //counts how much the compared element is similar to the original element
    double compareAttributeValues(String originAttrValue, String comparedAttrValue, String attributeName);
}

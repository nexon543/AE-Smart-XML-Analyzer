package com.agileengine.service;

import static com.agileengine.util.Constants.ATTRIBUTE_WEIGHT_PROPERTY_PREFIX;
import static com.agileengine.util.Constants.CONFIG_FILE_NAME;
import static com.agileengine.util.Constants.DEFAULT_ATTRIBUTE_WEIGHT;
import static com.agileengine.util.Constants.DEFAULT_ATTRIBUTE_WEIGHT_KEY;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAttributesComparisonService implements AttributesComparisonService {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleAttributesComparisonService.class);

    private Map<String, Double> attributeWeights;
    private double defaultWeight;

    public SimpleAttributesComparisonService() {
        final ResourceBundle properties = ResourceBundle.getBundle(CONFIG_FILE_NAME);
        final Enumeration<String> propKeys = properties.getKeys();
        attributeWeights=new HashMap<>();

        while(propKeys.hasMoreElements()){
            String key=propKeys.nextElement();
            attributeWeights.put(key.substring(ATTRIBUTE_WEIGHT_PROPERTY_PREFIX.length()), Double.valueOf(properties.getString(key)));
        }
        defaultWeight=Optional.of(attributeWeights.get(DEFAULT_ATTRIBUTE_WEIGHT_KEY)).orElse(DEFAULT_ATTRIBUTE_WEIGHT);
        LOGGER.info("attribute weights were initialized");
        LOGGER.info(attributeWeights.toString());
    }

    //core algorithm which calculates the degree of attribute similarity
    //can be enhanced by calculating more precise similarity of strings or adding synonyms processing
    @Override
    public double compareAttributeValues(String originValue, String comparedValue, String attributeName) {
        double result=0;
        if (originValue.trim().compareToIgnoreCase(comparedValue.trim()) == 0){
            result = 1;
        }
        result*= Optional.ofNullable(attributeWeights.get(attributeName)).orElse(defaultWeight);
        return result;
    }
}

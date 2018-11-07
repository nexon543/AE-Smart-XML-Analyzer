package com.agileengine.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Element;

public class ElementComparisonResult {

    @Getter
    @Setter
    private Element comparedElement;

    @Getter
    @Setter
    private Element originElement;

    @Getter
    private double totalComparisonResult;

    @Getter
    private Map<String, Double> similarAttributeWeights;

    public ElementComparisonResult() {
        similarAttributeWeights=new HashMap<>();
    }

    public void addComparisonResult(String attrName, double value){
        similarAttributeWeights.put(attrName, value);
        countAndSetTotalComparisonResult(similarAttributeWeights);
    }

    public ElementComparisonResult(
        Map<String, Double> similarAttributeWeights) {
        this.similarAttributeWeights = similarAttributeWeights;
        countAndSetTotalComparisonResult(similarAttributeWeights);
    }

    public void setSimilarAttributeWeights(Map<String, Double> similarAttributeWeights){
        this.similarAttributeWeights=similarAttributeWeights;
        countAndSetTotalComparisonResult(similarAttributeWeights);
    }

    private void countAndSetTotalComparisonResult(Map<String, Double> attrWeights){
        totalComparisonResult =0;
        attrWeights.entrySet().forEach(e-> totalComparisonResult +=e.getValue());
    }
}

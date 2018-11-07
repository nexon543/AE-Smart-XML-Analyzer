package com.agileengine.app;

import com.agileengine.model.ElementComparisonResult;
import com.agileengine.service.ElementSearchService;
import com.agileengine.service.ElementSearchServiceImpl;
import com.agileengine.util.Validator;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        //validate arguments
        if (!Validator.validateMainArgs(args)) {
            LOGGER.info("invalid arguments. Termination");
            LOGGER.info(
                "the arguments should be: <origin_file_path> <other_sample_file_path> <element_id>");
            return;
        }
        LOGGER.info("arguments are valid");

        //read arguments
        final String originalFilePath = args[0];
        final String modifiedFilePath = args[1];
        final String originElementId = args[2];

        final ElementSearchService elementSearchService = new ElementSearchServiceImpl(
            originalFilePath);
        Optional<Element> originElementOpt = elementSearchService
            .findElementById(originalFilePath, originElementId);
        if (!originElementOpt.isPresent()) {
            LOGGER.info(
                "origin element with id = [" + originElementId + "] was not found in document "
                    + originalFilePath);
            return;
        }

        LOGGER.info("the origin element is " + originElementOpt.get());
        final Optional<ElementComparisonResult> comparisonResult = elementSearchService
            .findSimilarElement(originElementOpt.get(), modifiedFilePath);
        if (!comparisonResult.isPresent()) {
            LOGGER.info(
                "Program did not manage to find any  similar element. Sorry. Try o adjust weights in cofig.properties or use"
                    + " another modified file.");
            return;
        }

        Map <String, Double> sortedAttributeWeights=comparisonResult.get().getSimilarAttributeWeights().entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        comparisonResult.get().setSimilarAttributeWeights(sortedAttributeWeights);
        printResult(comparisonResult.get(), originalFilePath, modifiedFilePath);
    }

    private static void printResult(ElementComparisonResult elementComparisonResult, String originalFilePath, String modifiedFilePath) {
        final double totalResult = elementComparisonResult.getTotalComparisonResult();
        final String attrNameHeader = "Attribute name";
        final String pointsHeader = "Points";
        final String influenceHeader = "Influence %";
        final String separator = "|";
        final String resultHeaderString =separator + attrNameHeader + separator + pointsHeader + separator + influenceHeader
            + separator;
        final DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);

        System.out.println("\n///////////////////////////////////RESULT SECTION\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
        String pathToTheElement=elementComparisonResult.getComparedElement().tagName();
        for(Element parent: elementComparisonResult.getComparedElement().parents()){
            pathToTheElement=parent.tagName()+"[id="+parent.attributes().get("id")+", class="+parent.attributes().get("class")+"]>"+pathToTheElement;
        }
        System.out.println("\nPath to the found element in the modified file: "+pathToTheElement+"\n\n");

        System.out.println("The element \"" + elementComparisonResult.getOriginElement()+"\" from "+originalFilePath);
        System.out.println("Was found as \"" + elementComparisonResult.getComparedElement()+"\" element in \n"+modifiedFilePath);
        System.out.println("\n");

        System.out.println("============================================");
        System.out.println("The total score of found element is: " + totalResult);
        System.out.println("============================================");
        System.out.println(StringUtils.repeat("-", resultHeaderString.length()));
        System.out.println(resultHeaderString);
        System.out.println(StringUtils.repeat("-", resultHeaderString.length()));
        for (Entry<String, Double> attrWeightEntry : elementComparisonResult
            .getSimilarAttributeWeights().entrySet()) {
            double currentAttrWeight = attrWeightEntry.getValue();
            System.out.println(
               separator + fixedLengthString(attrWeightEntry.getKey(), attrNameHeader.length()) + separator
                    + fixedLengthString(String.valueOf(currentAttrWeight), pointsHeader.length())
                    + separator
                    + fixedLengthString(df.format(currentAttrWeight / totalResult), influenceHeader.length()) + separator);
        }
        System.out.println(StringUtils.repeat("-", resultHeaderString.length()));
        System.out.println("Note: zero values can be adjusted with smarter algorithm implementation of the AttributeComparisonService interface");
    }

    private static String fixedLengthString(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }
}

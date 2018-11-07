package com.agileengine.service;

import static com.agileengine.util.Constants.TAG_NAME_WEIGHT_KEY;
import static com.agileengine.util.Constants.TEXT_WEIGHT_KEY;

import com.agileengine.model.ElementComparisonResult;
import java.io.File;
import java.util.Collections;
import java.util.Optional;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementSearchServiceImpl implements ElementSearchService {

    private static Logger LOGGER = LoggerFactory.getLogger(ElementSearchServiceImpl.class);
    private static String CHARSET_NAME = "utf8";

    @Getter
    private String htmlFilePath;
    private File htmlFile;
    private AttributesComparisonService attributesComparisonService;

    public ElementSearchServiceImpl(String htmlFilePath) {
        this.htmlFilePath = htmlFilePath;
        htmlFile = new File(htmlFilePath);
        attributesComparisonService = new SimpleAttributesComparisonService();
    }

    @Override
    public Optional<Element> findElementById(String filePath, String targetElementId) {
        if (!filePath.equals(htmlFilePath)) {
            setHtmlFilePath(filePath);
        }
        return findElementById(targetElementId);
    }

    //the core logic is written here
    @Override
    public Optional<ElementComparisonResult> findSimilarElement(Element originalElement,
        String filePath) {

        final Optional<Elements> allElements = findAllElements(filePath);
        if (!allElements.isPresent()) {
            LOGGER.info("no elements was found in the modified file");
            return Optional.empty();
        }

        ElementComparisonResult mostSimilarElementComparisonResult = new ElementComparisonResult(
            Collections.emptyMap());
        for (Element comparedElement : allElements.get()) {
            final ElementComparisonResult currentElementComparisonResult = compareElementAttributes(
                originalElement, comparedElement);

            if (currentElementComparisonResult.getTotalComparisonResult()
                > mostSimilarElementComparisonResult.getTotalComparisonResult()) {

                mostSimilarElementComparisonResult = currentElementComparisonResult;
                currentElementComparisonResult.setComparedElement(comparedElement);
            }
        }
        mostSimilarElementComparisonResult.setOriginElement(originalElement);

        if (mostSimilarElementComparisonResult.getTotalComparisonResult() == 0){
            return Optional.empty();
        }

        return Optional.of(mostSimilarElementComparisonResult);
    }

    public void setHtmlFilePath(String htmlFilePath) {
        this.htmlFilePath = htmlFilePath;
        this.htmlFile = new File(htmlFilePath);
    }

    private ElementComparisonResult compareElementAttributes(
        Element originalElement,
        Element comparedElement) {
        final ElementComparisonResult result = new ElementComparisonResult();

        for (Attribute originalAttribute : originalElement.attributes().asList()) {
            String comparedAttrValue = comparedElement.attributes().get(originalAttribute.getKey());
            if (comparedAttrValue != null) {
                addComparisonToTheResult(result, originalAttribute.getValue(), comparedAttrValue,
                    originalAttribute.getKey());
            }
        }
        addComparisonToTheResult(result, originalElement.tagName(),
            comparedElement.tagName(), TAG_NAME_WEIGHT_KEY);
        addComparisonToTheResult(result, originalElement.text(),
            comparedElement.text(), TEXT_WEIGHT_KEY);

        return result;
    }

    private void addComparisonToTheResult(ElementComparisonResult result, String firstValue,
        String secondValue, String attributeName) {
        double compareResult = attributesComparisonService
            .compareAttributeValues(firstValue, secondValue, attributeName);
        result.addComparisonResult(attributeName, compareResult);
    }

    private Optional<Elements> findAllElements(String filePath) {
        setHtmlFilePath(filePath);
        try {
            Document doc = Jsoup.parse(
                htmlFile,
                CHARSET_NAME,
                htmlFile.getAbsolutePath());

            return Optional.of(doc.getAllElements());

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Element> findElementById(String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                htmlFile,
                CHARSET_NAME,
                htmlFile.getAbsolutePath());

            return Optional.of(doc.getElementById(targetElementId));

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

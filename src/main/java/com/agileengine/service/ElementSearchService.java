package com.agileengine.service;

import com.agileengine.model.ElementComparisonResult;
import java.util.Optional;
import org.jsoup.nodes.Element;

public interface ElementSearchService {

    Optional<Element> findElementById(String filePath, String targetElementId);

    //searches the element in the file that is similar to the original element
    Optional<ElementComparisonResult> findSimilarElement(Element originalElement, String filePath);
}

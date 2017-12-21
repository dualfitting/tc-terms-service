package com.appirio.service.terms.manager;

import java.util.List;

/**
 * DocusignHandler is used to handle the document
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public interface DocusignHandler {
    
    /**
     * Handle document
     *
     * @param userId the userId to use
     * @param tabs the tabs to use
     * @throws DocusignHandlerException if any error occurs
     */
    public void handleDocument(long userId, List<String> tabs) throws DocusignHandlerException;

}

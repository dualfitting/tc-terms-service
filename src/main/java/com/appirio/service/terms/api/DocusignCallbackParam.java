package com.appirio.service.terms.api;


import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * The DocusignCallbackParam entity.
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class DocusignCallbackParam {
    /**
     * Represents the envelope status attribute.
     */
    @Getter
    @Setter
    private String envelopeStatus;
    
    /**
     * Represents the envelope id attribute.
     */
    @Getter
    @Setter
    private String envelopeId;
    
    /**
     * Represents the connect key attribute.
     */
    @Getter
    @Setter
    private String connectKey;
    
    /**
     * Represents the tabs attribute.
     */
    @Getter
    @Setter
    private List<String> tabs;
    
}
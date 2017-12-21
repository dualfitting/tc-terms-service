package com.appirio.service.terms.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the DocusignEnvelope entity.
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class DocusignEnvelope {
    /**
     * Represents the docusign envelope id attribute.
     */
    @Getter
    @Setter
    private String docusignEnvelopeId;
    
    /**
     * Represents the docusign template id attribute.
     */
    @Getter
    @Setter
    private String docusignTemplateId;
    
    /**
     * Represents the user id attribute.
     */
    @Getter
    @Setter
    private long userId;
    
    /**
     * Represents the is completed attribute.
     */
    @Getter
    @Setter
    private boolean isCompleted;
    
}
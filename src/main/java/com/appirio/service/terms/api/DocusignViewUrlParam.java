package com.appirio.service.terms.api;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the DocusignViewUrlParam entity.
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class DocusignViewUrlParam {
    /**
     * Represents the template id attribute.
     */
    @Getter
    @Setter
    private String templateId;

    /**
     * Represents the tabs attribute.
     */
    @Getter
    @Setter
    private List<String> tabs;

    /**
     * The returnUrl field
     */
    @Getter
    @Setter
    private String returnUrl;
}


/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the TermsOfUse entity.
 * 
 * @author TCCoder
 * @version 1.0
 */
public class TermsOfUse {
    /**
     * Represents the terms of use id attribute.
     */
    @Getter
    @Setter
    private Long termsOfUseId;


    /**
     * Represents the title attribute.
     */
    @Getter
    @Setter
    private String title;


    /**
     * Represents the url attribute.
     */
    @Getter
    @Setter
    private String url;


    /**
     * Represents the text attribute.
     */
    @Getter
    @Setter
    private String text;


    /**
     * Represents the agreeability type id attribute.
     */
    @Getter
    @Setter
    @JsonIgnore
    private Long agreeabilityTypeId;


    /**
     * Represents the docusign template id attribute.
     */
    @Getter
    @Setter
    private String docusignTemplateId;


    /**
     * Represents the agreeability type attribute.
     */
    @Getter
    @Setter
    private String agreeabilityType;


    /**
     * Represents the agreed attribute.
     */
    @Getter
    @Setter
    private Boolean agreed;

}
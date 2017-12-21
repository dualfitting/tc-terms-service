/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * The SmtpConfiguration is for configuration.
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class SmtpConfiguration {
    /**
     * Represents the smtp host attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String smtpHost;
    
    /**
     * Represents the smtp port attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private int smtpPort = 25;
    
    /**
     * Represents the smtp sender attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String smtpSender;
    
    /**
     * Represents the smtp secured attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private boolean smtpSecured;
    
    /**
     * Represents the smtp username attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private String smtpUsername;
    
    /**
     * Represents the smtp password attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private String smtpPassword;
    
}
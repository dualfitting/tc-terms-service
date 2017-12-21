package com.appirio.service.terms.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * The DocusignConfiguration is for the docusign configuration.
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 */
public class DocusignConfiguration {
    /**
     * Represents the username attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String username;
    
    /**
     * Represents the password attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String password;
    
    /**
     * Represents the integrator key attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String integratorKey;
    
    /**
     * Represents the serverurl attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String serverURL;
    
    /**
     * Represents the role name attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String roleName;
    
    /**
     * Represents the client user id attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String clientUserId;
    
    /**
     * Represents the returnurl attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String returnURL;
    
    /**
     * Represents the assignmentv2 template id attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String assignmentV2TemplateId;
    
    /**
     * Represents the w9 template id attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String w9TemplateId;
    
    /**
     * Represents the w8ben template id attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String w8benTemplateId;
    
    /**
     * Represents the appirio mutual NDA template id attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String appirioMutualNDATemplateId;
    
    /**
     * Represents the affidavit template id attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String affidavitTemplateId;
    
    /**
     * Represents the assignment doc terms of use id attribute.
     */
    @JsonProperty
    @Getter
    @Setter
    private long assignmentDocTermsOfUseId;
    
    /**
     * Represents the callback failed email subject attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String callbackFailedEmailSubject;
    
    /**
     * Represents the callback connect key attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String callbackConnectKey;
    
    /**
     * Represents the support email address attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String supportEmailAddress;
    
    /**
     * Represents the from email address attribute.
     */
    @JsonProperty
    @NotEmpty
    @Getter
    @Setter
    private String fromEmailAddress;
}
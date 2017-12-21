/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms;

import com.appirio.service.BaseAppConfiguration;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;
import com.appirio.service.terms.config.DocusignConfiguration;
import com.appirio.service.terms.config.SmtpConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for the terms service
 *
 * @author TCSCODER
 * @version 1.0
 */
public class TermsServiceConfiguration extends BaseAppConfiguration {

    /**
     * Datasources
     */
    @Valid
    @NotNull
    @JsonProperty
    private List<SupplyDatasourceFactory> databases = new ArrayList<>();
    
    /**
     * Represents the docusignConfiguration attribute.
     */
    @Valid
    @NotNull
    @JsonProperty("docusignConfiguration")
    private final DocusignConfiguration docusignConfiguration = new DocusignConfiguration();
    
    
    /**
     * Represents the smtpConfiguration attribute.
     */
    @Valid
    @NotNull
    @JsonProperty("smtpConfiguration")
    private final SmtpConfiguration smtpConfiguration = new SmtpConfiguration();

    /**
     * Get the data source factory
     *
     * @return Data source factory
     */
    public List<SupplyDatasourceFactory> getDatabases() {
        return databases;
    }

    /**
     * Get docusignConfiguration.
     * @return the docusignConfiguration. 
     */
    public DocusignConfiguration getDocusignConfiguration() {
        return this.docusignConfiguration;
    }
    

    /**
     * Get smtpConfiguration.
     * @return the smtpConfiguration. 
     */
    public SmtpConfiguration getSmtpConfiguration() {
        return this.smtpConfiguration;
    }
}

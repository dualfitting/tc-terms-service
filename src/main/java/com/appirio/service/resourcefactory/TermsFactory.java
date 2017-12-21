/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.resourcefactory;

import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.service.terms.TermsServiceConfiguration;
import com.appirio.service.terms.dao.TermsDAO;
import com.appirio.service.terms.manager.TermsManager;
import com.appirio.service.terms.resources.TermsResource;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;

/**
 * TermsFactory is used to create TermsResource 
 * 
 * Version 1.1 - Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * - create the TermsManager with the configuration
 * 
 * @author TCCoder
 * @version 1.1 
 *
 */
public class TermsFactory implements ResourceFactory<TermsResource> {
    
    /**
     * The termsServiceConfiguration field
     */
    private TermsServiceConfiguration termsServiceConfiguration;
    
    /**
     * Create TermsFactory
     *
     * @param termsServiceConfiguration the termsServiceConfiguration to use
     */
    public TermsFactory(TermsServiceConfiguration termsServiceConfiguration) {
        this.termsServiceConfiguration = termsServiceConfiguration;
    }

    /**
     * Get TermsResource object
     *
     * @return the TermsResource instance
     * @throws SupplyException if error occurs
     */
    @Override
    public TermsResource getResourceInstance() throws SupplyException {
        final TermsManager TermsManager = new TermsManager(
                DAOFactory.getInstance().createDAO(TermsDAO.class), this.termsServiceConfiguration);
        return new TermsResource(TermsManager);
    }
}

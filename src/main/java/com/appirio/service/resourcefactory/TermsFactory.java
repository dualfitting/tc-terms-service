/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.resourcefactory;

import com.appirio.service.supply.resources.ResourceFactory;
import com.appirio.service.terms.dao.TermsDAO;
import com.appirio.service.terms.manager.TermsManager;
import com.appirio.service.terms.resources.TermsResource;
import com.appirio.supply.DAOFactory;
import com.appirio.supply.SupplyException;

/**
 * TermsFactory is used to create TermsResource 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class TermsFactory implements ResourceFactory<TermsResource> {

    /**
     * Get TermsResource object
     *
     * @return the TermsResource instance
     * @throws SupplyException if error occurs
     */
    @Override
    public TermsResource getResourceInstance() throws SupplyException {
        final TermsManager TermsManager = new TermsManager(
                DAOFactory.getInstance().createDAO(TermsDAO.class));
        return new TermsResource(TermsManager);
    }
}

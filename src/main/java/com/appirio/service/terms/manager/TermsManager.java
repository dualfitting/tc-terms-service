/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms.manager;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.appirio.service.terms.api.TermsOfUse;
import com.appirio.service.terms.dao.TermsDAO;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.auth.AuthUser;

/**
 * TermsManager is used to manage the terms of use for the user. 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class TermsManager {
    
    /**
     * The AGREEABILITY_TYPE_IF_FOR_DOC_SIGN_TEMPLATE constant
     */
    private static final long AGREEABILITY_TYPE_ID_FOR_DOC_SIGN_TEMPLATE = 4;
    
    /**
     * The ELECTRONICALLY_AGREEABLE contstant
     */
    private static final String ELECTRONICALLY_AGREEABLE = "Electronically-agreeable";
    
    /**
     * The termsDao used to manage the terms of use data
     */
    private TermsDAO termsDao;
    
    /**
     * Create TermsManager
     *
     * @param termsDao the termsDao to use
     */
    public TermsManager(TermsDAO termsDao) {
        this.termsDao = termsDao;
    }
    
    /**
     * Get terms of use
     *
     * @param authUser the authUser to use
     * @param termsOfUseId the termsOfUseId to use
     * @param noAuth the noAuth to use
     * @throws SupplyException if error occurs
     * @return the TermsOfUse result
     */
    public TermsOfUse getTermsOfUse(AuthUser authUser, long termsOfUseId, boolean noAuth) throws SupplyException {
        if (authUser == null && !noAuth) {
            throw new SupplyException("Authentication credential was missing.", HttpServletResponse.SC_UNAUTHORIZED);
        }
        if (termsOfUseId <= 0) {
            throw new SupplyException("The termsOfUseId should be positive.", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        TermsOfUse termsOfUse = null;
        if (noAuth) {
            termsOfUse = termsDao.getTermsOfUseNoAuth(termsOfUseId);
        } else {
            termsOfUse = termsDao.getTermsOfUse(Long.parseLong(authUser.getUserId().getId()), termsOfUseId);
        }
        if (termsOfUse == null) {
            throw new SupplyException("No such terms of use exists.", HttpServletResponse.SC_NOT_FOUND);
        }
        if (termsOfUse.getAgreeabilityTypeId() != null && termsOfUse.getAgreeabilityTypeId() == AGREEABILITY_TYPE_ID_FOR_DOC_SIGN_TEMPLATE) {
            if (termsOfUse.getDocusignTemplateId() == null) {
                throw new SupplyException("Docusign template id is missing.", HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            termsOfUse.setDocusignTemplateId(null);
        }
        termsOfUse.setAgreeabilityTypeId(null);
        
        return termsOfUse;
    }
    
    /**
     * Agree terms of use
     *
     * @param authUser the authUser to use
     * @param termsOfUseId the termsOfUseId to use
     * @throws SupplyException if error occurs
     */
    public void agreeTermsOfUse(AuthUser authUser, long termsOfUseId) throws SupplyException {
        if (termsOfUseId <= 0) {
            throw new SupplyException("The termsOfUseId should be positive.", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        long userId = Long.parseLong(authUser.getUserId().getId());
        TermsOfUse termsOfUse = termsDao.getTermsOfUse(userId, termsOfUseId);
        
        if (termsOfUse == null) {
            throw new SupplyException("No such terms of use exists.", HttpServletResponse.SC_NOT_FOUND);
        }
        if (!ELECTRONICALLY_AGREEABLE.equals(termsOfUse.getAgreeabilityType())) {
            throw new SupplyException("The terms is not electronically agreeable.", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        if (termsOfUse.getAgreed() != null && termsOfUse.getAgreed()) {
            throw new SupplyException("You have agreed to this terms of use before.", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        List<TCID> ids = termsDao.getDependencyTermsOfUse(userId, termsOfUseId);
        if (ids != null) {
            for (TCID id : ids) {
                if (id == null || id.getId() == null) {
                    throw new SupplyException("You can't agree to this terms of use before you have agreed to all the dependencies terms of use.", HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }
        
        if (termsDao.checkUserTermsOfUseBan(userId, termsOfUseId) != null) {
            throw new SupplyException("Sorry, you can not agree to this terms of use.", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        termsDao.addUserTermsOfUse(userId, termsOfUseId);
    }

}

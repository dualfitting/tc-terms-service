package com.appirio.service.terms.manager;

import java.util.List;

import org.apache.log4j.Logger;

import com.appirio.service.terms.api.TermsOfUse;
import com.appirio.service.terms.dao.TermsDAO;

/**
 * DefaultDocusignHandler is used the handle the docusign document
 * 
 * It's added in Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class DefaultDocusignHandler implements DocusignHandler {
    /**
     * The logger field
     */
    private static final Logger logger = Logger.getLogger(DefaultDocusignHandler.class);

    /**
     * The termsDao field
     */
    private TermsDAO termsDao;

    /**
     * The termsOfUseId field
     */
    private long termsOfUseId;
    
    /**
     * Create DefaultDocusignHandler
     *
     * @param termsDao the termsDao to use
     * @param termsOfUseId the termsOfUseId to use
     */
    public DefaultDocusignHandler(TermsDAO termsDao, long termsOfUseId) {
        this.termsDao = termsDao;
        this.termsOfUseId = termsOfUseId;
    }

    /**
     * Handle document
     *
     * @param userId the userId to use
     * @param tabs the tabs to use
     * @throws DocusignHandlerException if any error occurs
     */
    public void handleDocument(long userId, List<String> tabs) throws DocusignHandlerException {
        try {
            TermsOfUse termsOfUse = termsDao.getTermsOfUse(userId, termsOfUseId);

            if (termsOfUse == null) {
                throw new DocusignHandlerException("No such terms of use exists.", false);
            }

            if (termsOfUse.getAgreed() != null && termsOfUse.getAgreed()) {
                logger.warn("User with id: " + userId + " has already accepted terms of use with id: " + termsOfUseId);
                return;
            }


            if (termsDao.checkUserTermsOfUseBan(userId, termsOfUseId) != null) {
                logger.error("User with id: " + userId + " is not allowed to accept terms of use with id: " + termsOfUseId);
                return;
            }

            termsDao.addUserTermsOfUse(userId, termsOfUseId);
        } catch (DocusignHandlerException de) {
            throw de;
        } catch (Exception spe) {
            throw new DocusignHandlerException("Unable to process terms of use. Try again later.", true);

        }
    }
}

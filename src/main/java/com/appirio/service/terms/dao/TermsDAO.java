/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;

import com.appirio.service.terms.api.TermsOfUse;
import com.appirio.supply.dataaccess.DatasourceName;
import com.appirio.supply.dataaccess.SqlQueryFile;
import com.appirio.supply.dataaccess.SqlUpdateFile;
import com.appirio.tech.core.api.v3.TCID;

/**
 * TermsDAO is used to manage the user terms 
 * 
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
@DatasourceName("oltp")
public interface TermsDAO {
    
    /**
     * Insert user terms of use 
     * 
     * @param userId the userId to use
     * @param termsOfUseId the termsOfUseId to use
     */
    @SqlUpdateFile("sql/terms/insert_user_terms_of_use.sql")
    void addUserTermsOfUse(@Bind("userId") long userId, @Bind("termsOfUseId") long termsOfUseId);
    
    /**
     * Check user terms of use ban 
     *
     * @param userId the userId to use
     * @param termsOfUseId the termsOfUseId to use
     * @return non-null TCID if exists, null otherwise
     */
    @SqlQueryFile("sql/terms/check_user_terms_of_use_ban.sql")
    TCID checkUserTermsOfUseBan(@Bind("userId") long userId, @Bind("termsOfUseId") long termsOfUseId);
    
    /**
     * Get dependency terms of use 
     *
     * @param userId the userId to use
     * @param termsOfUseId the termsOfUseId to use
     * @return a list of dependency ids
     */
    @SqlQueryFile("sql/terms/get_dependency_terms_of_use.sql")
    List<TCID> getDependencyTermsOfUse(@Bind("userId") long userId, @Bind("termsOfUseId") long termsOfUseId);
    
    /**
     * Get terms of use with no authorization
     *
     * @param termsOfUseId the termsOfUseId to use
     * @return the TermsOfUse result
     */
    @SqlQueryFile("sql/terms/get_terms_of_use_noauth.sql")
    TermsOfUse getTermsOfUseNoAuth(@Bind("termsOfUseId") long termsOfUseId);
    
    /**
     * Get terms of use 
     *
     * @param userId the userId to use
     * @param termsOfUseId the termsOfUseId to use
     * @return the TermsOfUse result
     */
    @SqlQueryFile("sql/terms/get_terms_of_use.sql")
    TermsOfUse getTermsOfUse(@Bind("userId") long userId, @Bind("termsOfUseId") long termsOfUseId);
}

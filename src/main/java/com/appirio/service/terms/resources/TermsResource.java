/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms.resources;

import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.service.terms.api.DocusignCallbackParam;
import com.appirio.service.terms.api.DocusignViewUrlParam;
import com.appirio.service.terms.api.TermsOfUse;
import com.appirio.service.terms.manager.TermsManager;
import com.appirio.supply.ErrorHandler;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.request.PostPutRequest;
import com.appirio.tech.core.api.v3.request.annotation.AllowAnonymous;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.appirio.tech.core.auth.AuthUser;
import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.Auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * TermsResource is used to add terms of use and get terms of use for user.
 * 
 * Version 1.1 - Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * - add the POST terms/docusignCallback and  POST terms/docusign/viewURL endpoints
 * 
 * 
 * @author TCSCODER
 * @version 1.1 
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("terms")
public class TermsResource {

    /**
     * The logger to log events
     */
    private static final Logger logger = LoggerFactory
            .getLogger(TermsResource.class);

    /**
     * Manager to manage the terms of use data.
     */
    private TermsManager termsManager;

    /**
     * Constructor that initializes the manager
     *
     * @param TermsManager the terms manager
     */
    public TermsResource(TermsManager TermsManager) {
        this.termsManager = TermsManager;
    }

    /**
     * Get terms of use
     *
     * @param termsOfUseId the termsOfUseId to use
     * @param noAuth the noAuth to use
     * @param securityContext the securityContext to use
     * @return the ApiResponse result
     */
    @GET
    @Path("/{termsOfUseId}")
    @Timed
    @AllowAnonymous
    public ApiResponse getTermsOfUse(@Valid @PathParam("termsOfUseId") long termsOfUseId, @QueryParam("noAuth") boolean noAuth, @Context SecurityContext securityContext) {
        try {
            logger.debug("Enter of getTermsOfUse method, termsOfUseId:" + termsOfUseId + ", noAuth:" + noAuth);
            AuthUser authUser = (AuthUser) securityContext.getUserPrincipal();
            TermsOfUse termsOfUse = termsManager.getTermsOfUse(authUser, termsOfUseId, noAuth);
            return MetadataApiResponseFactory.createResponse(termsOfUse);
        } catch (Exception e) {
            logger.error("Error in getTermsOfUse", e);
            return ErrorHandler.handle(e, logger);
        }
    }
    
    /**
     * Agree terms of use
     *
     * @param authUser the authUser to use
     * @param termsOfUseId the termsOfUseId to use
     * @return the ApiResponse result
     */
    @POST
    @Path("/{termsOfUseId}/agree")
    @Timed
    public ApiResponse agreeTermsOfUse(@Auth AuthUser authUser, @Valid @PathParam("termsOfUseId") long termsOfUseId) {
        try {
            logger.debug("Enter of agreeTermsOfUse method, termsOfUseId:" + termsOfUseId);
            termsManager.agreeTermsOfUse(authUser, termsOfUseId);
            return MetadataApiResponseFactory.createResponse(null);
        } catch (Exception e) {
            logger.error("Error in agreeTermsOfUse", e);
            return ErrorHandler.handle(e, logger);
        }
    }
    
    /**
     * Get docusign view url
     *
     * @param authUser the authUser to use
     * @param request the request to use
     * @return the ApiResponse result
     */
    @POST
    @Path("/docusignViewURL")
    @Timed
    public ApiResponse getDocusignViewURL(@Auth AuthUser authUser, @Valid PostPutRequest<DocusignViewUrlParam> request) {
        try {
            logger.debug("Enter of getDocusignViewURL method");
            if (request == null) {
                throw new SupplyException("Please provide the post body.", HttpServletResponse.SC_BAD_REQUEST);
            }
            return MetadataApiResponseFactory.createResponse(termsManager.getDocusignViewURL(authUser, request.getParam()));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }
    
    /**
     * Docusign callback method
     *
     * @param authUser the authUser to use
     * @param request the request to use
     * @return the ApiResponse result
     */
    @POST
    @Path("/docusignCallback")
    @Timed
    public ApiResponse docusignCallback(@Auth AuthUser authUser, @Valid PostPutRequest<DocusignCallbackParam> request) {
        try {
            logger.debug("Enter of docusignCallback method");
            if (request == null) {
                throw new SupplyException("Please provide the post body.", HttpServletResponse.SC_BAD_REQUEST);
            }
            return MetadataApiResponseFactory.createResponse(termsManager.docusignCallback(request.getParam()));
        } catch (Exception e) {
            return ErrorHandler.handle(e, logger);
        }
    }

    /**
     * Checks the health.
     *
     * @return the ApiResponse result
     */
    @GET
    @Path("/healthcheck")
    @Timed
    @AllowAnonymous
    public ApiResponse checkHealth() {
        return MetadataApiResponseFactory.createResponse(null);
    }
}

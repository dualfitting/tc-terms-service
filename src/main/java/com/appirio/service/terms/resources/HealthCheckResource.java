/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms.resources;

import com.appirio.service.supply.resources.MetadataApiResponseFactory;
import com.appirio.tech.core.api.v3.request.annotation.AllowAnonymous;
import com.appirio.tech.core.api.v3.response.ApiResponse;
import com.codahale.metrics.annotation.Timed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * HealthCheckResource is used to basically health check.
 * 
 * @author FireIce
 * @version 1.1 
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("terms")
public class HealthCheckResource {
    /**
     * Constructor
     */
    public HealthCheckResource() {
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
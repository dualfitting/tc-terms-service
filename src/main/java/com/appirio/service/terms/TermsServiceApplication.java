/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms;

import com.appirio.service.BaseApplication;
import com.appirio.service.resourcefactory.TermsFactory;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * The entry point for the terms micro service
 *  
 * Version 1.1 - Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * - create TermsFactory with the configuration
 *
 * Version 1.2 Topcoder - Revise Configuration For Terms Service. 
 * - Added initialize() method.
 * 
 * @author TCSCODER
 * @version 1.2
 */
public class TermsServiceApplication extends BaseApplication<TermsServiceConfiguration> {
    /**
     * Refer to APIApplication
     */
    @Override
    public String getName() {
        return "terms-service";
    }

    /**
     * Initializes the application to enable variable substitution with environment variables.
     *
     * @since 1.2
     */
    @Override
    public void initialize(Bootstrap<TermsServiceConfiguration> bootstrap) {
        super.initialize(bootstrap);
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap
            .getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    }
    
    /**
     * Log service specific configuration values.
     * 
     * @param config the configuration
     */
    @Override
    protected void logServiceSpecificConfigs(TermsServiceConfiguration config) {
        for(SupplyDatasourceFactory dbConfig : config.getDatabases()) {
            logger.info("\tJDBI configuration ");
            logger.info("\t\tDatabase config name : " + dbConfig.getDatasourceName());
            logger.info("\t\tOLTP driver class : " + dbConfig.getDriverClass());
            logger.info("\t\tOLTP connection URL : " + dbConfig.getUrl());
            logger.info("\t\tOLTP Authentication user : " + dbConfig.getUser());
        }
        logger.info("\r\n");
    }

    /**
     * Application entrypoint. See dropwizard and jetty documentation for more details
     *
     * @param args       arguments to main
     * @throws Exception Generic exception
     */
    public static void main(String[] args) throws Exception {
        new TermsServiceApplication().run(args);
    }

    /**
     * Gives the subclasses an opportunity to register resources
     * 
     * @param config the configuration
     * @param env the environment
     */
    @Override
    protected void registerResources(TermsServiceConfiguration config, Environment env) throws Exception {
        env.jersey().register(new TermsFactory(config).getResourceInstance());

        logger.info("Services registered");
    }

    /**
     * Gives the subclasses an opportunity to prepare to run
     * 
     * @param config the configuration
     * @param env the environment
     */
    @Override
    protected void prepare(TermsServiceConfiguration config, Environment env) throws Exception {
        // configure jackson
        env.getObjectMapper().addMixIn(XMLGregorianCalendar.class, MixIn.class);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        env.getObjectMapper().setDateFormat(dateFormat);
        env.getObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // get configuration from env
        configDatabases(config, config.getDatabases(), env);
    }
    
    
    /**
     * Fix the XMLGregorianCalendar deserialization bug in jackson
     */
    public interface MixIn {
        /**
         * Ignore this method during deserialization.
         * 
         * @param year the year
         */
        @JsonIgnore
        void setYear(BigInteger year);
    }
}

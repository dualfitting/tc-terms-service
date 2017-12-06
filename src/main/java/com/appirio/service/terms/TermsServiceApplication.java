/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.terms;

import com.appirio.service.BaseApplication;
import com.appirio.service.resourcefactory.TermsFactory;
import com.appirio.service.supply.resources.SupplyDatasourceFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.setup.Environment;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * The entry point for the terms micro service
 * 
 *
 * @author TCSCODER
 * @version 1.0
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
        env.jersey().register(new TermsFactory().getResourceInstance());

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

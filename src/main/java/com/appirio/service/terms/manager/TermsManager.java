package com.appirio.service.terms.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.appirio.service.terms.TermsServiceConfiguration;
import com.appirio.service.terms.api.DocusignCallbackParam;
import com.appirio.service.terms.api.DocusignEnvelope;
import com.appirio.service.terms.api.DocusignViewUrlParam;
import com.appirio.service.terms.api.TermsOfUse;
import com.appirio.service.terms.dao.TermsDAO;
import com.appirio.supply.SupplyException;
import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.auth.AuthUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TermsManager is used to manage the terms of use for the user. 
 * 
 * Version 1.1 - Topcoder - Re-implement Docusign Related APIs - Terms Service v1.0
 * - add the getDocusignViewURL and docusignCallback methods for docusign
 * 
 * @author TCCoder
 * @version 1.1 
 *
 */
public class TermsManager {
    
    /**
     * The logger field
     */
    private static final Logger logger = Logger.getLogger(TermsManager.class);
    
    /**
     * The AGREEABILITY_TYPE_IF_FOR_DOC_SIGN_TEMPLATE constant
     */
    private static final long AGREEABILITY_TYPE_ID_FOR_DOC_SIGN_TEMPLATE = 4;
    
    /**
     * The ELECTRONICALLY_AGREEABLE contstant
     */
    private static final String ELECTRONICALLY_AGREEABLE = "Electronically-agreeable";
    
    /**
     * The TEMPLATE_ID_PATTERN field to check the template id
     */
    private static final Pattern TEMPLATE_ID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    
    /**
     * The SSL_FACTORY field
     */
    private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    
    /**
     * The termsServiceConfiguration field
     */
    private TermsServiceConfiguration termsServiceConfiguration;
    
    /**
     * The termsDao used to manage the terms of use data
     */
    private TermsDAO termsDao;
    
    /**
     * The templateHandlers field
     */
    private Map<String, List<DocusignHandler>> templateHandlers = new HashMap<String, List<DocusignHandler>>();
    
    /**
     * The docusignAuthHeader field
     */
    private MultivaluedMap<String, Object> docusignAuthHeader = new MultivaluedHashMap<String, Object>();
    
    /**
     * Create TermsManager
     *
     * @param termsDao the termsDao to use
     * @param termsServiceConfiguration the termsServiceConfiguration to use
     * @throws SupplyException if any error occurs
     */
    public TermsManager(TermsDAO termsDao, TermsServiceConfiguration termsServiceConfiguration) throws SupplyException {
        this.termsDao = termsDao;
        this.termsServiceConfiguration = termsServiceConfiguration;
        
        Map<String, String> auth = new LinkedHashMap<String, String>();
        auth.put("Username", this.termsServiceConfiguration.getDocusignConfiguration().getUsername());
        auth.put("Password", this.termsServiceConfiguration.getDocusignConfiguration().getPassword());
        auth.put("IntegratorKey", this.termsServiceConfiguration.getDocusignConfiguration().getIntegratorKey());
        try {
            this.docusignAuthHeader.add("X-DocuSign-Authentication", new ObjectMapper().writeValueAsString(auth));
        } catch (Exception exp) {
            throw new SupplyException(exp);
        }
        
        List<DocusignHandler> handlers = new ArrayList<DocusignHandler>();
        handlers.add(new DefaultDocusignHandler(this.termsDao, this.termsServiceConfiguration.getDocusignConfiguration().getAssignmentDocTermsOfUseId()));
        this.templateHandlers.put(this.termsServiceConfiguration.getDocusignConfiguration().getAppirioMutualNDATemplateId(), handlers);
        this.templateHandlers.put(this.termsServiceConfiguration.getDocusignConfiguration().getAffidavitTemplateId(), new ArrayList<DocusignHandler>());
        this.templateHandlers.put(this.termsServiceConfiguration.getDocusignConfiguration().getAssignmentV2TemplateId(), new ArrayList<DocusignHandler>());
        this.templateHandlers.put(this.termsServiceConfiguration.getDocusignConfiguration().getW8benTemplateId(), new ArrayList<DocusignHandler>());
        this.templateHandlers.put(this.termsServiceConfiguration.getDocusignConfiguration().getW9TemplateId(), new ArrayList<DocusignHandler>());
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
    
    /**
     * Get docusign view url
     *
     * @param authUser the authUser to use
     * @param docusignViewUrlParam the docusignViewUrlParam to use
     * @throws SupplyException if any error occurs
     * @return the Map<String,String> result contains the view url and envelop id
     */
    public Map<String, String> getDocusignViewURL(AuthUser authUser, DocusignViewUrlParam docusignViewUrlParam) throws SupplyException {
        if (docusignViewUrlParam == null) {
            throw new SupplyException("Docusign view url param is missing", HttpServletResponse.SC_BAD_REQUEST);

        }
        if (docusignViewUrlParam.getTemplateId() == null) {
            throw new SupplyException("template id is null", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (docusignViewUrlParam.getReturnUrl() != null && docusignViewUrlParam.getReturnUrl().trim().length() == 0) {
            throw new SupplyException("The return url should not be empty string", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (!TEMPLATE_ID_PATTERN.matcher(docusignViewUrlParam.getTemplateId()).matches()) {
            throw new SupplyException("templateId is not a valid uuid.", HttpServletResponse.SC_BAD_REQUEST);
        }
        Map<String, String> result = new LinkedHashMap<String, String>();
        
        long userId = Long.parseLong(authUser.getUserId().getId());
        String templateId = docusignViewUrlParam.getTemplateId();

        List<String> tabsParam = docusignViewUrlParam.getTabs() != null ? docusignViewUrlParam.getTabs() : new ArrayList<String>();
        String[][] tabs = new String[tabsParam.size()][2];
        for (int i = 0; i < tabsParam.size(); ++i) {
            String[] subs = tabsParam.get(i).split("\\|\\|");
            if (subs.length != 2) {
                throw new SupplyException("tabs parameter is not in correct format. Key values must be a separated by a || (double pipe).",
                        HttpServletResponse.SC_BAD_REQUEST);
            }
            tabs[i] = subs;
        }

        String baseUrl = "";
        try {
            WebTarget target = ClientBuilder.newClient().target(this.termsServiceConfiguration.getDocusignConfiguration().getServerURL() + "login_information");
            Response response = target.request(MediaType.APPLICATION_JSON).headers(docusignAuthHeader).get();
            JsonNode node = response.readEntity(JsonNode.class);
            if (response.getStatus() == HttpServletResponse.SC_OK || response.getStatus() == HttpServletResponse.SC_CREATED) {
                JsonNode loginAccounts = node.path("loginAccounts");
                for (JsonNode sub : loginAccounts) {
                    baseUrl = sub.path("baseUrl").textValue();
                    break;
                }
            } else {
                if (node.path("message").textValue() != null) {
                    logger.error(node.path("message").textValue());
                }
                
                throw new SupplyException("Login to DocuSign server failed.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SupplyException se) {
            throw se;
        } catch (Exception exp) {
            SupplyException se = new SupplyException("Login to DocuSign server failed.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }

        Map<String, Object> user = this.termsDao.getUser(userId);
        List<DocusignEnvelope> envelopes = this.termsDao.getDocusignEnvelope(userId, docusignViewUrlParam.getTemplateId());
        String envelopeId = null;
        if (envelopes == null || envelopes.isEmpty()) {
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("templateId", templateId);
            body.put("status", "sent");
            body.put("enableWetSign", false);
            Map<String, Object> templateRoles = new HashMap<String, Object>();
            templateRoles.put("name", user.get("firstName").toString() + " " + user.get("lastName").toString());
            templateRoles.put("email", user.get("email").toString());
            templateRoles.put("roleName", this.termsServiceConfiguration.getDocusignConfiguration().getRoleName());
            templateRoles.put("clientUserId", this.termsServiceConfiguration.getDocusignConfiguration().getClientUserId());
            Map<String, Object> tabsMap = new HashMap<String, Object>();

            List<Map<String, Object>> textTabs = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < tabs.length; ++i) {
                Map<String, Object> sub = new HashMap<String, Object>();
                sub.put("tabLabel", tabs[i][0]);
                sub.put("value", tabs[i][1]);
                textTabs.add(sub);
            }

            tabsMap.put("textTabs", textTabs);
            templateRoles.put("tabs", tabsMap);
            
            List<Map<String, Object>> trs = new ArrayList<Map<String, Object>>();
            trs.add(templateRoles);
            body.put("templateRoles", trs);

            try {
                WebTarget target = ClientBuilder.newClient().target(baseUrl + "/envelopes");
                Response response = target.request(MediaType.APPLICATION_JSON).headers(docusignAuthHeader).post(Entity.json(body));
                if (response.getStatus() == HttpServletResponse.SC_OK || response.getStatus() == HttpServletResponse.SC_CREATED) {
                    JsonNode node = response.readEntity(JsonNode.class);
                    envelopeId = node.path("envelopeId").textValue();
                    this.termsDao.addDocusignEnvelope(envelopeId, templateId, userId, false);
                } else {
                    JsonNode node = response.readEntity(JsonNode.class);
                    if (node.path("message").textValue() != null) {
                        logger.error(node.path("message").textValue());
                    }
                    String error = node.path("errorCode").textValue();
                    if ("TEMPLATE_ID_INVALID".equals(error)) {
                        throw new SupplyException("Template with given id was not found.", HttpServletResponse.SC_NOT_FOUND);
                    }
                    throw new SupplyException("Requesting Signature via template failed.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (SupplyException se) {
                throw se;
            } catch (Exception exp) {
                SupplyException se = new SupplyException("Requesting Signature via template failed.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw se;
            }
        } else {
           envelopeId = envelopes.get(0).getDocusignEnvelopeId();
        }
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("userName", user.get("firstName").toString() + " " + user.get("lastName").toString());
        body.put("email", user.get("email").toString());
        body.put("returnUrl", docusignViewUrlParam.getReturnUrl() != null ? docusignViewUrlParam.getReturnUrl() : 
            String.format(this.termsServiceConfiguration.getDocusignConfiguration().getReturnURL(), envelopeId));
        body.put("clientUserId", this.termsServiceConfiguration.getDocusignConfiguration().getClientUserId());
        body.put("authenticationMethod", "none");
        try {
            WebTarget target = ClientBuilder.newClient().target(baseUrl + "/envelopes/" + envelopeId + "/views/recipient");
            Response response = target.request(MediaType.APPLICATION_JSON).headers(docusignAuthHeader).post(Entity.json(body));
            if (response.getStatus() == HttpServletResponse.SC_OK || response.getStatus() == HttpServletResponse.SC_CREATED) {
                JsonNode node = response.readEntity(JsonNode.class);
                String url = node.path("url").textValue();
                result.put("recipientViewUrl", url);
                result.put("envelopeId", envelopeId);
            } else {
                JsonNode node = response.readEntity(JsonNode.class);
                if (node.path("message").textValue() != null) {
                    logger.error(node.path("message").textValue());
                }
                throw new SupplyException("Requesting recipient view failed.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SupplyException se) {
            throw se;
        } catch (Exception exp) {
            SupplyException se = new SupplyException("Requesting Signature via template failed.", exp);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
        return result;
    }
    
    
    /**
     * Docusign callback method
     *
     * @param docusignCallbackParam the docusignCallbackParam to use
     * @throws SupplyException if any error occurs
     */
    public Map<String, String> docusignCallback(DocusignCallbackParam docusignCallbackParam) throws SupplyException {
        if (docusignCallbackParam == null) {
            throw new SupplyException("docusignCallbackParam is missing.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (!this.termsServiceConfiguration.getDocusignConfiguration().getCallbackConnectKey().equals(docusignCallbackParam.getConnectKey())) {
            throw new SupplyException("Connect Key is missing or invalid.", HttpServletResponse.SC_BAD_REQUEST);
        }
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (!"Completed".equals(docusignCallbackParam.getEnvelopeStatus())) {
            logger.info("Status is not completed.");
            return result;
        }
        if (docusignCallbackParam.getEnvelopeId() == null || docusignCallbackParam.getEnvelopeId().trim().length() == 0) {
            logger.error("envelopeId is null or empty");
            return result;
        }
        
        DocusignEnvelope envelope = this.termsDao.getDocusignEnvelopeByEnvelopeId(docusignCallbackParam.getEnvelopeId());
        if (envelope == null) {
            logger.error("No envelope with id: " + docusignCallbackParam.getEnvelopeId() + " was found.");
            return result;
        } else {
            this.termsDao.completeDocusignEnvelope(docusignCallbackParam.getEnvelopeId());
            envelope.setCompleted(true);
            if (!this.templateHandlers.keySet().contains(envelope.getDocusignTemplateId())) {
                logger.warn("No Template was found for template id: " + envelope.getDocusignTemplateId());
            } else {
                try {
                    List<DocusignHandler> handlers = this.templateHandlers.get(envelope.getDocusignTemplateId());
                    for (DocusignHandler handler : handlers) {
                        handler.handleDocument(envelope.getUserId(), docusignCallbackParam.getTabs());
                    }
                } catch (DocusignHandlerException exp) {
                    this.sendMail(exp.getMessage(), envelope);
                    if (exp.isTemporary()) {
                        throw new SupplyException("Internal Server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    } else {
                        result.put("message", exp.getMessage());
                        return result;
                    }
                }
            }
        }
        result.put("message", "success");
        return result;
        
    }
   
    /**
     * Send mail
     *
     * @param errorMessage the errorMessage to use
     * @param envelope the envelope to use
     * @throws SupplyException if any error occurs
     */
    private void sendMail(String errorMessage, DocusignEnvelope envelope) throws SupplyException {
        String template = "docusign_callback_failure_email";

        Map<String, String> vs = new HashMap<String, String>();
        vs.put("userId", envelope.getUserId() + "");
        vs.put("templateId", envelope.getDocusignTemplateId());
        vs.put("envelopeId", envelope.getDocusignEnvelopeId());
        vs.put("message", errorMessage);

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream("templates/email/" + template);
            byte[] by = new byte[is.available()];
            is.read(by, 0, by.length);
            String templateData = new String(by);
            for (String key : vs.keySet()) {
                templateData = templateData.replace("<%= " + key + " %>", vs.get(key));
            }

            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.host", this.termsServiceConfiguration.getSmtpConfiguration().getSmtpHost());
            properties.setProperty("mail.smtp.port", this.termsServiceConfiguration.getSmtpConfiguration().getSmtpPort() + "");
            if (this.termsServiceConfiguration.getSmtpConfiguration().isSmtpSecured()) {
                properties.setProperty("mail.smtp.auth", "true");
            }
            
            Session session;
            if (this.termsServiceConfiguration.getSmtpConfiguration().isSmtpSecured()) {
                properties.put("mail.smtp.socketFactory.class", SSL_FACTORY); 
                properties.put("mail.smtp.socketFactory.fallback", "false");
                properties.put("mail.smtp.socketFactory.port", this.termsServiceConfiguration.getSmtpConfiguration().getSmtpPort() + "");
            } 
            
            if (this.termsServiceConfiguration.getSmtpConfiguration().getSmtpUsername() != null) {
                session = Session.getDefaultInstance(properties, new MyAuthenticator(this.termsServiceConfiguration.getSmtpConfiguration().getSmtpUsername(), 
                        this.termsServiceConfiguration.getSmtpConfiguration().getSmtpPassword()));
            } else {
                session = Session.getDefaultInstance(properties);
            }
           
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.termsServiceConfiguration.getDocusignConfiguration().getFromEmailAddress()));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(this.termsServiceConfiguration.getDocusignConfiguration().getSupportEmailAddress()));

            message.setSubject(this.termsServiceConfiguration.getDocusignConfiguration().getCallbackFailedEmailSubject());
            message.setText(templateData);
            Transport.send(message);
        } catch (Exception exp) {
            SupplyException se = new SupplyException("Failed to send the email", exp);
            se.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw se;
        }
    }

    /**
     * The smtp authenticator
     */
    private static class MyAuthenticator extends Authenticator {

        /**
         * The username field
         */
        private String username;


        /**
         * The password field
         */
        private String password;

        /**
         * Create MyAuthenticator
         *
         * @param username the username to use
         * @param password the password to use
         */
        public MyAuthenticator(String username, String password) {
            super();
            this.username = username;
            this.password = password;
        }

        /**
         * Get password authentication
         *
         * @return the PasswordAuthentication result
         */
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(username, password);
        }
    }
}

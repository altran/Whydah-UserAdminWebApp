package net.whydah.identity.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.identity.config.AppConfig;
import net.whydah.identity.data.ApplicationCredential;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.MissingResourceException;
import java.util.Properties;

public class SSOHelper {
    private static final Logger logger = LoggerFactory.getLogger(SSOHelper.class);
    public static final String USER_TOKEN_REFERENCE_NAME = "whydahusertoken_sso";

    private final URI tokenServiceUri;
    private final Client tokenServiceClient = Client.create();
    private String myAppTokenXml;
    private String myAppTokenId;
    private String myUserTokenId;


    public SSOHelper() throws IOException {
        try {
            tokenServiceUri = UriBuilder.fromUri(AppConfig.readProperties().getProperty("tokenservice")).build();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
    }
    /*
    public void logonApplication() {
        PostMethod p = setUpApplicationLogon();
        HttpClient c = new HttpClient();
        try {
            int v = c.executeMethod(p);
            if (v == 201) {
                logger.info("Post" + p.getRequestHeader("Location").getValue());
            }
            if (v == 400) {
                logger.info("Internal error");
            }
            if (v == 500 || v == 501) {
                logger.info("Internal error");
// retry
            }
            logger.info(p.getResponseBodyAsString());

        } catch (IOException e) {
            logger.error("", e);
        } finally {
            p.releaseConnection();
        }
    }
    */

    private void logonApplication() {
        //todo sjekke om myAppTokenXml er gyldig før reauth
        WebResource logonResource = tokenServiceClient.resource(tokenServiceUri).path("logon");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        ApplicationCredential appCredential = new ApplicationCredential();
        try {
            String applicationid = AppConfig.readProperties().getProperty("applicationid");
            String applicationsecret = AppConfig.readProperties().getProperty("applicationsecret");

            appCredential.setApplicationID(applicationid);
            appCredential.setApplicationPassord(applicationsecret);

            formData.add("applicationcredential", appCredential.toXML());
            ClientResponse response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
            //todo håndtere feil i statuskode + feil ved app-pålogging (retry etc)
            if (response.getStatus() != 200) {
                logger.error("Application authentication failed with statuscode {}", response.getStatus());
                throw new RuntimeException("Application authentication failed");
            }
            myAppTokenXml = response.getEntity(String.class);
            myAppTokenId = XPATHHelper.getApplicationTokenIdFromAppTokenXML(myAppTokenXml);
            logger.debug("Applogon ok: apptokenxml: {}", myAppTokenXml);
            logger.debug("myAppTokenId: {}", myAppTokenId);
        } catch (IOException ioe){
            logger.warn("Did not find configuration for my application credential.",ioe);
        }
    }


    public String getMyAppTokenId(){
        return myAppTokenId;
    }

    public String getMyUserTokenId(){
        return myUserTokenId;
    }


    public String getUserTokenFromUserTokenId(String usertokenid) {
        logonApplication();
        WebResource userTokenResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_usertokenid");
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("usertokenid", usertokenid);
        ClientResponse response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == ClientResponse.Status.FORBIDDEN.getStatusCode()) {
            throw new IllegalArgumentException("getUserTokenFromUserTokenId - get_usertoken_by_usertokenid failed.");
        }
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.trace("Response OK with XML: {}", responseXML);
            return responseXML;
        }
        //retry
        response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.trace("Response OK with XML: {}", responseXML);
            return responseXML;
        }

        return null;
    }

    public static void removeUserTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getUserTokenCookie(request);
        if(cookie != null) {
            cookie.setValue(USER_TOKEN_REFERENCE_NAME);
            cookie.setMaxAge(0);
            cookie.setValue("");
            response.addCookie(cookie);
        }
    }

    public static  Cookie getUserTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        System.out.println("=============> header: " + cookies);
        if (cookies == null) {
            return null;
        }

        for (Cookie cooky : cookies) {
            System.out.println("Cookie: " + cooky.getName());
            if (cooky.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                return cooky;
            }
        }
        return null;
    }



    private PostMethod setUpGetUserToken(PostMethod p,String userTokenId) throws IOException {
        String appTokenXML = p.getResponseBodyAsString();
        String applicationtokenid = XPATHHelper.getApplicationTokenIdFromAppTokenXML(appTokenXML);
        WebResource resource = tokenServiceClient.resource(tokenServiceUri).path("user/" + applicationtokenid + "/get_usertoken_by_usertokenid");

        PostMethod p2 = new PostMethod(resource.toString());
        p2.addParameter("apptoken",appTokenXML);
        p2.addParameter("usertokenid",userTokenId);

        logger.trace("apptoken:" + appTokenXML);
        logger.trace("usertokenid:" + userTokenId);
        return p2;
    }



    private PostMethod setupRealApplicationLogon() {
        ApplicationCredential acred = new ApplicationCredential();
        try {
            acred = new ApplicationCredential();
            Properties properties = AppConfig.readProperties();

            acred.setApplicationID(properties.getProperty("applicationname"));
            acred.setApplicationPassord(properties.getProperty("applicationname"));

        } catch (IOException ioe) {
            logger.error("Unable to get my application credentials from propertyfile.", ioe);
        }
        WebResource resource = tokenServiceClient.resource(tokenServiceUri).path("/logon");

        PostMethod p = new PostMethod(resource.toString());
        p.addParameter("applicationcredential",acred.toXML());
        return p;
    }


    public String getUserTokenByUserTicket(String userticket) {
        logonApplication();


        WebResource userTokenResource = tokenServiceClient.resource(tokenServiceUri).path("user/" + myAppTokenId + "/get_usertoken_by_userticket");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("userticket", userticket);
        ClientResponse response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == ClientResponse.Status.FORBIDDEN.getStatusCode()) {
            throw new IllegalArgumentException("Login failed.");
        }
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.trace("Response OK with XML: {}", responseXML);
            myUserTokenId = XPATHHelper.getUserTokenIdFromUserTokenXML(responseXML);
            return responseXML;
        }
        //retry
        response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.trace("Response OK with XML: {}", responseXML);
            return responseXML;
        }
        logger.warn("User authentication failed: {}", response);
        if (response.getStatus() == Response.Status.GONE.getStatusCode()) {
        	throw new MissingResourceException("No token found for ticket.", getClass().getSimpleName(), userticket);
        }
        throw new RuntimeException("User authentication failed with status code " + response.getStatus());
    }

    public Cookie createUserTokenCookie(String userTokenXml) {
        String usertokenID = XPATHHelper.getUserTokenIdFromUserTokenXML(userTokenXml);
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, usertokenID);
        //int maxAge = calculateTokenRemainingLifetime(userTokenXml);
        int maxAge = 365 * 24 * 60 * 60; //TODO Calculating TokenLife is hindered by XML with differing schemas

        cookie.setMaxAge(maxAge);
        cookie.setValue(usertokenID);
        cookie.setSecure(true);
        logger.trace("Created cookie with name=" + USER_TOKEN_REFERENCE_NAME + ", usertokenID=" + usertokenID + ", maxAge=" + maxAge);
        return cookie;
    }


    private int calculateTokenRemainingLifetime(String userxml) {
        int tokenLifespan = Integer.parseInt(XPATHHelper.getLifespan(userxml));
        long tokenTimestamp = Long.parseLong(XPATHHelper.getTimestamp(userxml));
        long endOfTokenLife = tokenTimestamp + tokenLifespan;
        long remainingLife_ms = endOfTokenLife - System.currentTimeMillis();
        return (int)remainingLife_ms/1000;
    }



    public String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        logger.trace("=============> header: " + cookies);
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            logger.trace("Cookie: " + cookie.getName());
            if (cookie.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                return cookie.getValue();
                //return true;
            }
        }
        return null;
    }

    /**
     * Look for cookie for whydah auth.
     * @param request
     * @return
     */
    public boolean hasRightCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        // logger.info("=============> header: " + cookies);
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            //logger.info("Cookie: " + cookie.getName());
            if (cookie.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                return true;
            }
        }
        return false;
    }
}


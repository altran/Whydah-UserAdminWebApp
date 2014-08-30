package net.whydah.identity.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.whydah.identity.config.AppConfig;
import net.whydah.identity.data.ApplicationCredential;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.MissingResourceException;

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

            //appCredential.setApplicationID("Whydah SSO UserAdministration");
            //appCredential.setApplicationPassord("secret dummy");

            formData.add("applicationcredential", appCredential.toXML());
            ClientResponse response = logonResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
            //todo håndtere feil i statuskode + feil ved app-pålogging (retry etc)
            if (response.getStatus() != 200) {
                logger.error("Application authentication failed with statuscode {}", response.getStatus());
                throw new RuntimeException("Application authentication failed");
            }
            myAppTokenXml = response.getEntity(String.class);
            myAppTokenId = getTokenIdFromAppToken(myAppTokenXml);
            logger.debug("Applogon ok: apptokenxml: {}", myAppTokenXml);
            logger.debug("myAppTokenId: {}", myAppTokenId);
        } catch (IOException ioe){
            logger.warn("Did not find configuration for my application credential.",ioe);
        }
    }
    private String getTokenIdFromAppToken(String appTokenXML) {
        return appTokenXML.substring(appTokenXML.indexOf("<applicationtokenID>") + "<applicationtokenID>".length(), appTokenXML.indexOf("</applicationtokenID>"));
    }


    public String getMyAppTokenId(){
        return myAppTokenId;
    }

    public String getMyUserTokenId(){
        return myUserTokenId;
    }


    private PostMethod setUpApplicationLogon() {
        String requestXML = "";
        WebResource resource = tokenServiceClient.resource(tokenServiceUri).path("/logon");
        PostMethod p = new PostMethod(resource.toString());
        p.addParameter("applicationcredential",requestXML);
        return p;
    }

    public String getUserToken(String usertokenid) {
        if (usertokenid==null){
            usertokenid="dummy";
        }
        PostMethod p = setupRealApplicationLogon();
        HttpClient c = new HttpClient();
        try {
            int v = c.executeMethod(p);
            if (v == 201) {
                logger.info("Post" + p.getRequestHeader("Location").getValue());
            }
            if (v == 400) {
                logger.info("Internal error");
            }
            if (v == 406) {
                logger.info("Not accepted");
            }
            if (v == 500 || v == 501) {
                logger.info("Internal error");
// retry
            }
            logger.info("ApplicationToken:" + p.getResponseBodyAsString());
            PostMethod p2 = setUpGetUserToken(p,usertokenid);
            v = c.executeMethod(p2);
            if (v == 201) {
                logger.info("Post" + p2.getRequestHeader("Location").getValue());
            }
            if (v == 400 || v == 404 ) {
                logger.info("Internal error");
            }
            if (v == 406) {
                logger.info("Not accepted");
            }
            if (v == 415 ) {
                logger.info("Internal error, unsupported media type");
            }
            if (v == 500 || v == 501) {
                logger.info("Internal error");// retry
            }
//            logger.info("Request:"+p2.
            logger.info("v:" + v);
            logger.info("Response:" + p2.getResponseBodyAsString());
            return p2.getResponseBodyAsString();


        } catch (IOException e) {
            logger.error("", e);
        } finally {
            p.releaseConnection();
        }
        return null;
    }


    private PostMethod setUpGetUserToken(PostMethod p,String userTokenid) throws IOException {
        String appTokenXML = p.getResponseBodyAsString();
        String applicationtokenid = appTokenXML.substring(appTokenXML.indexOf("<applicationtokenID>") + "<applicationtokenID>".length(), appTokenXML.indexOf("</applicationtokenID>"));
        WebResource resource = tokenServiceClient.resource(tokenServiceUri).path("/token/" + applicationtokenid + "/getusertokenbytokenid");

        PostMethod p2 = new PostMethod(resource.toString());
        p2.addParameter("apptoken",appTokenXML);
        p2.addParameter("usertokenid",userTokenid);

        logger.info("apptoken:" + appTokenXML);
        logger.info("usertokenid:" + userTokenid);
        return p2;
    }

    private PostMethod setupRealApplicationLogon() {
        ApplicationCredential acred = new ApplicationCredential();
        acred.setApplicationID("Whydah");
        acred.setApplicationPassord("dummy");

        WebResource resource = tokenServiceClient.resource(tokenServiceUri).path("/logon");

        PostMethod p = new PostMethod(resource.toString());
        p.addParameter("applicationcredential",acred.toXML());
        return p;
    }


    public String getUserTokenByTicket(String ticket) {
        logonApplication();


        WebResource userTokenResource = tokenServiceClient.resource(tokenServiceUri).path("token/" + myAppTokenId + "/getusertokenbyticket");
        MultivaluedMap<String,String> formData = new MultivaluedMapImpl();
        formData.add("apptoken", myAppTokenXml);
        formData.add("ticket", ticket);
        ClientResponse response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == ClientResponse.Status.FORBIDDEN.getStatusCode()) {
            throw new IllegalArgumentException("Login failed.");
        }
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("Response OK with XML: {}", responseXML);
            myUserTokenId = getTokenId(responseXML);
            return responseXML;
        }
        //retry
        response = userTokenResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            String responseXML = response.getEntity(String.class);
            logger.debug("Response OK with XML: {}", responseXML);
            return responseXML;
        }
        logger.warn("User authentication failed: {}", response);
        if (response.getStatus() == Response.Status.GONE.getStatusCode()) {
        	throw new MissingResourceException("No token found for ticket.", getClass().getSimpleName(), ticket);
        }
        throw new RuntimeException("User authentication failed with status code " + response.getStatus());
    }

    public Cookie createUserTokenCookie(String userTokenXml) {
        String tokenID = getTokenId(userTokenXml);
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, tokenID);
        //int maxAge = calculateTokenRemainingLifetime(userTokenXml);
        int maxAge = 365 * 24 * 60 * 60; //TODO Calculating TokenLife is hindered by XML with differing schemas

        cookie.setMaxAge(maxAge);
        cookie.setValue(tokenID);
        cookie.setSecure(true);
        logger.debug("Created cookie with name=" + USER_TOKEN_REFERENCE_NAME + ", tokenID=" + tokenID + ", maxAge=" + maxAge);
        return cookie;
    }
    public String getTokenId(String userTokenXml) {
        if (userTokenXml == null) {
            logger.debug("Empty  userToken");
            return "";
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/token/@id";
            XPathExpression xPathExpression = xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }
    private int calculateTokenRemainingLifetime(String userxml) {
        int tokenLifespan = Integer.parseInt(getLifespan(userxml));
        long tokenTimestamp = Long.parseLong(getTimestamp(userxml));
        long endOfTokenLife = tokenTimestamp + tokenLifespan;
        long remainingLife_ms = endOfTokenLife - System.currentTimeMillis();
        return (int)remainingLife_ms/1000;
    }

    private String getLifespan(String userTokenXml) {
        if (userTokenXml == null){
            logger.debug("Empty  userToken");
            return "";
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/whydahuser/identity/lifespan";
            XPathExpression xPathExpression = xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }

    private String getTimestamp(String userTokenXml) {
        if (userTokenXml==null){
            logger.debug("Empty  userToken");
            return "";
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/whydahuser/identity/timestamp";
            XPathExpression xPathExpression = xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }

    public String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        // logger.info("=============> header: " + cookies);
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            //logger.debug("Cookie: " + cookie.getName());
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


package net.whydah.identity.web;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import net.whydah.identity.config.AppConfig;
import net.whydah.identity.util.SSOHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.MissingResourceException;
import java.util.Properties;

@Controller
public class UserAdminController {
    private static final Logger logger = LoggerFactory.getLogger(UserAdminController.class);
    public static final String USERTICKET = "userticket";
    private static final int MIN_USERTICKET_LENGTH = 7;
    private static final int MIN_USER_TOKEN_LENGTH = 11;
    private static final int MIN_USERTOKEN_ID_LENGTH = 4;
    private static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";

    private SSOHelper ssoHelper = new SSOHelper();

    private String MY_APP_TYPE = "myapp";
    private final String MY_APP_URI;
    private final String LOGIN_SERVICE;
    private final String userIdentityBackend;
    private final String LOGOUT_SERVICE;
    private final HttpClient httpClient;
    private final boolean STANDALONE;

    public UserAdminController() throws IOException {
        Properties properties = AppConfig.readProperties();
        STANDALONE = Boolean.valueOf(properties.getProperty("standalone"));
        MY_APP_URI = properties.getProperty("myuri");
        MY_APP_TYPE = properties.getProperty("myapp");
        userIdentityBackend = properties.getProperty("useridentitybackend");

        LOGIN_SERVICE = "redirect:" + properties.getProperty("logonserviceurl") + "login?redirectURI=" + MY_APP_URI;
        LOGOUT_SERVICE = properties.getProperty("logonserviceurl") + "logoutaction?redirectURI=" + MY_APP_URI;

        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

        StringBuilder strb = new StringBuilder("Initialized UserAdminController \n");
        strb.append("\n- Standalone=").append(STANDALONE);
        strb.append("\n- MY_APP_URI=").append(MY_APP_URI);
        strb.append("\n- userIdentityBackend=").append(userIdentityBackend);
        strb.append("\n- LOGIN_SERVICE=").append(LOGIN_SERVICE);
        strb.append("\n- LOGOUT_SERVICE=").append(LOGOUT_SERVICE);
        logger.debug(strb.toString());
    }

    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    @RequestMapping("/")
    public String myapp(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setContentType(HTML_CONTENT_TYPE);
        if (STANDALONE) {
            logger.debug("Standalone mode select, no authentication.");
            addModelParams(model, null);
            return MY_APP_TYPE;
            // return "myapp";
        }

        String userTicket = request.getParameter(USERTICKET);
        logger.debug("userTicket:" + userTicket);
        try {
	        if (userTicket != null && userTicket.length() > MIN_USERTICKET_LENGTH) {
	        	
	            String userTokenXml = ssoHelper.getUserTokenByTicket(userTicket);
	            logger.debug("userToken from ticket:" + userTokenXml);
	            if (userTokenXml.length() >= MIN_USER_TOKEN_LENGTH) {
	                String tokenId = ssoHelper.getTokenId(userTokenXml);
	                logger.debug("tokenId:" + tokenId);
	                addModelParams(model, tokenId);
	
	
	                Cookie cookie = ssoHelper.createUserTokenCookie(userTokenXml);
	                // cookie.setDomain("whydah.net");
	                response.addCookie(cookie);
	
	                //return "myapp";
	                return MY_APP_TYPE;
	            } else {
	                return LOGIN_SERVICE;
	            }
	        }
        } catch (MissingResourceException mre) {
        	logger.debug("The ticked might have already been used, checking the cookie.", mre);
        }

        if (ssoHelper.hasRightCookie(request)) {
            String userTokenIdFromCookie = ssoHelper.getUserTokenIdFromCookie(request);
            logger.debug("userTokenIdFromCookie=" + userTokenIdFromCookie);
            String userTokenXmlFromCookie = ssoHelper.getUserToken(userTokenIdFromCookie);
            logger.debug("userTokenXmlFromCookie=" + userTokenXmlFromCookie);

            if (userTokenXmlFromCookie.length() >= MIN_USER_TOKEN_LENGTH ) {

                addModelParams(model, userTokenIdFromCookie);

                // TODO verify that the token is valid

                //TODO Should we do something with the cookie here?
                //return "myapp";
                return MY_APP_TYPE;
            } else {
                return LOGIN_SERVICE;
            }
        }
        return LOGIN_SERVICE;
    }


    private void addModelParams(Model model, String userTokenID) {
        if (userTokenID != null && userTokenID.length() >= MIN_USERTOKEN_ID_LENGTH) {
            model.addAttribute("token", ssoHelper.getUserToken(userTokenID));
            model.addAttribute("logOutUrl", LOGOUT_SERVICE);
            model.addAttribute("realName", getRealName(ssoHelper.getUserToken(userTokenID)));
        } else {
            model.addAttribute("token", "Unauthorized");
            model.addAttribute("logOutUrl", LOGOUT_SERVICE);
            model.addAttribute("realName", "Unknown UA");
        }

        String baseUrl = "/useradmin/" + ssoHelper.getMyAppTokenId() + "/" + ssoHelper.getMyUserTokenId()+"/";
        model.addAttribute("baseUrl", baseUrl);
    }

    private String getRealName(String userTokenXml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/token/firstname[1]";
            XPathExpression xPathExpression =  xPath.compile(expression);
            String fornavn = (xPathExpression.evaluate(doc));
            expression = "/token/lastname[1]";
            xPathExpression = xPath.compile(expression);
            String etternavn = (xPathExpression.evaluate(doc));
            return fornavn + " " + etternavn;
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }

}

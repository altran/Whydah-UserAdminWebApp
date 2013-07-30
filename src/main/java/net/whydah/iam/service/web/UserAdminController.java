package net.whydah.iam.service.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.whydah.iam.service.config.AppConfig;
import net.whydah.iam.service.util.SSOHelper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

@Controller
public class UserAdminController {
    private static final Logger logger = LoggerFactory.getLogger(UserAdminController.class);
    public static final String USERTICKET = "userticket";
    private static final int MIN_USERTICKET_LENGTH = 7;
    private static final int MIN_USER_TOKEN_LENGTH = 11;
    private static final int MIN_USERTOKEN_ID_LENGTH = 4;
    private static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";


    private SSOHelper ssoHelper = new SSOHelper();

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

    @RequestMapping("/")
    public String myapp(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setContentType(HTML_CONTENT_TYPE);
        if (STANDALONE) {
            logger.debug("Standalone mode select, no authentication.");
            addModelParams(model, null);
            return "myapp";
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
	
	                return "myapp";
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

            if (userTokenXmlFromCookie.length() >= MIN_USER_TOKEN_LENGTH) {
                addModelParams(model, userTokenIdFromCookie);

                //TODO Should we do something with the cookie here?
                return "myapp";
            } else {
                return LOGIN_SERVICE;
            }
        }
        return LOGIN_SERVICE;
    }


    private void addModelParams(Model model, String userTokenID) {
        if (userTokenID != null && userTokenID.length() >= MIN_USERTOKEN_ID_LENGTH) {
            model.addAttribute("token", ssoHelper.getUserToken(userTokenID));
            model.addAttribute("logouturl", LOGOUT_SERVICE);
            model.addAttribute("realname", getRealName(ssoHelper.getUserToken(userTokenID)));
        } else {
            model.addAttribute("token", "Unauthorized");
            model.addAttribute("logouturl", LOGOUT_SERVICE);
            model.addAttribute("realname", "Unknown UA");
        }

        model.addAttribute("myHost", MY_APP_URI );
        model.addAttribute("myHostJson", MY_APP_URI + "json");
        model.addAttribute("myHostJsonPost", MY_APP_URI + "jsonp");
        model.addAttribute("myHostJsonPut", MY_APP_URI + "jsonpu");

        String userAdminUrl = MY_APP_URI + "json?url=" + userIdentityBackend + "useradmin/" + ssoHelper.getMyUserTokenId()+"/";
        String userAdminPuUrl = MY_APP_URI + "jsonpu?url=" + userIdentityBackend + "useradmin/" + ssoHelper.getMyUserTokenId()+"/";
        String userAdminPUrl = MY_APP_URI + "jsonp?url=" + userIdentityBackend + "useradmin/" + ssoHelper.getMyUserTokenId()+"/";

        logger.trace("Adding admin urls to modelParams");
        model.addAttribute("myHostJsonUsers", userAdminUrl + "users/");
        model.addAttribute("myHostJsonUserFind", userAdminUrl + "find/");

        model.addAttribute("myHostJsonUserUpdate", userAdminPuUrl + "users/");
        model.addAttribute("myHostJsonUserAdd", userAdminPuUrl + "users/add");

        //model.addAttribute("myHostJsonUserDelete", MY_APP_URI +"json?url=" + userIdentityBackend + "useradmin/users/"); //OLD
        model.addAttribute("myHostJsonUserDelete", userAdminPUrl + "users/");  //New
        model.addAttribute("myHostJsonRoleAdd", userAdminPUrl + "users/");
        model.addAttribute("myHostJsonRoleDelete", userAdminPUrl + "users/"); //New
        //model.addAttribute("myHostUserDelete", MY_APP_URI +"jsonp?url=" + userIdentityBackend + "useradmin/users/"); //OLD

        model.addAttribute("myHostUserDelete", userAdminPUrl + "users/"); //NEW work with it

        model.addAttribute("myHostUserAdd", userAdminPUrl + "users/add");  //For adding user
        model.addAttribute("myHostJsonCustomerSearch", userAdminUrl + "persons/find/");
        //model.addAttribute("myHostUserAdd", MY_APP_URI);

        logger.trace("Finished adding modelParams");
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



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequestMapping("/json")
    public String json(@PathParam("url") String url, HttpServletRequest request, HttpServletResponse response, Model model) {
        HttpMethod method = new GetMethod();
        HttpMethodParams params = new HttpMethodParams();
        params.setHttpElementCharset("UTF-8");
        params.setContentCharset("UTF-8");
        method.setParams(params);
        logger.trace("Accessing /json with url:" + url);
        //logger.info("getHost:"+getHost());
        try {
            method.setURI(new URI(url, true));
            int rescode = httpClient.executeMethod(method); 
            // TODO: check rescode?
            if (rescode != 200) {
            	// Do something
            }
            
            InputStream responseBodyStream = method.getResponseBodyAsStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(responseBodyStream));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = in.readLine()) !=null) {
            	responseBody.append(line);
            }
            model.addAttribute("jsondata", responseBody.toString());
            response.setContentType("application/json; charset=utf-8");
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            method.releaseConnection();
        }
        if (!model.containsAttribute("jsondata")) {
        	logger.error("jsondata attribute not set when fetching data from URL: {}", url);
        }
        return "json";
    }


    //Adding user
    @GET
    @RequestMapping("/jsonp")
    @Path("/jsonp/{url}/add/{jsond}") //OLD
    public String jsonp(@PathParam("jsond") String jsond, @PathParam("url") String url, HttpServletRequest request, HttpServletResponse response, Model model) { //OLD
        try {
            logger.debug("Accessing /jsonp with url=" + url + ", request=" + request.getParameter("jsond") + ", jsond=" + jsond);
            java.net.URI baseUri = UriBuilder.fromUri(url).build();
            WebResource webResource = Client.create().resource(baseUri);

            if (jsond ==null) {
                jsond ="test";
                logger.info("jsondata is NULL");
                logger.info("jsond is" + jsond);
                String sa = webResource.type("application/json").post(String.class, jsond);
                model.addAttribute("jsondata", sa); //OLD
            } else {
                String s = webResource.type("application/json").post(String.class, jsond);
                model.addAttribute("jsondata", s); //OLD	
                logger.info("jsondata is NOT NULL");
            }
        } catch (Exception e) {
            logger.error("", e);
            model.addAttribute("jsondata", "");
        }
        return "json";
    }


    //Editing user
    @GET
    @RequestMapping("/jsonpu")
    @Path("/jsonpu/{url}/add/{jsond}")
    public String jsonpu(@PathParam("jsond") String jsond, @PathParam("url") String url, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            logger.info(" url2:" + url);
            logger.info("req2:" + request.getParameter("jsond"));
            java.net.URI baseUri = UriBuilder.fromUri(url).build();
            WebResource webResource = Client.create().resource(baseUri);
            String s = webResource.type("application/json").put(String.class, jsond);
            model.addAttribute("jsondata", s);
        } catch (Exception e) {
            logger.error("", e);
            model.addAttribute("jsondata", "");
        }
        return "json";
    }



//
//    public static String getHost() {
//        String host = "localhost";
//        try {
//            String hostName = InetAddress.getLocalHost().getHostName();
//
//            InetAddress addrs[] = InetAddress.getAllByName(hostName);
//
//            String myIp = "UNKNOWN";
//            for (InetAddress addr : addrs) {
//                //logger.info("addr.getHostAddress() = " + addr.getHostAddress());
//                //logger.info("addr.getHostName() = " + addr.getHostName());
//                //logger.info("addr.isAnyLocalAddress() = " + addr.isAnyLocalAddress());
//                //logger.info("addr.isLinkLocalAddress() = " + addr.isLinkLocalAddress());
//                //logger.info("addr.isLoopbackAddress() = " + addr.isLoopbackAddress());
//                //logger.info("addr.isMulticastAddress() = " + addr.isMulticastAddress());
//                //logger.info("addr.isSiteLocalAddress() = " + addr.isSiteLocalAddress());
//                //logger.info("");
//
//                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
//                    myIp = addr.getHostAddress();
//                }
//            }
//            // logger.info("\nIP = " + myIp);
//            host = myIp;
//        } catch (UnknownHostException e) {
//        }
//        return host;
//    }

}

package net.whydah.identity.admin;

import net.whydah.identity.admin.config.AppConfig;
import net.whydah.identity.admin.usertoken.TokenServiceClient;
import net.whydah.identity.admin.usertoken.UserTokenXpathHelper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
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

    private TokenServiceClient tokenServiceClient = new TokenServiceClient();

    private String MY_APP_TYPE = "myapp";
    private final String MY_APP_URI;
    private final String LOGIN_SERVICE;
    private final String userIdentityBackend;
    private final String LOGOUT_SERVICE;
    private final HttpClient httpClient;
    private final boolean STANDALONE;
    Properties properties = AppConfig.readProperties();

    public UserAdminController() throws IOException {
        STANDALONE = Boolean.valueOf(properties.getProperty("standalone"));
        MY_APP_URI = properties.getProperty("myuri");
        MY_APP_TYPE = properties.getProperty("myapp");
        if (MY_APP_TYPE == null || MY_APP_TYPE.isEmpty()) {
            MY_APP_TYPE = "useradmin"; //TODO To be fixed in https://github.com/altran/Whydah-UserAdminWebApp/issues/44
        }
        userIdentityBackend = properties.getProperty("useridentitybackend");

        LOGIN_SERVICE = "redirect:" + properties.getProperty("logonservice") + "login?redirectURI=" + MY_APP_URI;
        LOGOUT_SERVICE = "redirect:" + properties.getProperty("logonservice") + "logoutaction?redirectURI=" + MY_APP_URI;

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
            logger.trace("myapp - Standalone mode select, no authentication.");
            addModelParams(model, null);
            return MY_APP_TYPE;
            // return "myapp";
        }

        String userTicket = request.getParameter(USERTICKET);
        logger.trace("myapp - userTicket:" + userTicket);
        try {
            if (userTicket != null && userTicket.length() > MIN_USERTICKET_LENGTH) {
                String userTokenXml = tokenServiceClient.getUserTokenByUserTicket(userTicket);
                logger.trace("myapp - userToken={} from userticket:", userTokenXml);
                if (userTokenXml.length() >= MIN_USER_TOKEN_LENGTH) {
                    String userTokenId = UserTokenXpathHelper.getUserTokenIdFromUserTokenXML(userTokenXml);
                    if (!UserTokenXpathHelper.hasUserAdminRight(userTokenXml)) {
                        logger.trace("Got user from userticket, but wrong access rights - logout");
                        return LOGOUT_SERVICE;
                    }
                    logger.trace("myapp - Got user from userticket - has correct access rights - usertokenId:" + userTokenId);
                    addModelParams(model, userTokenId);


                    CookieManager.createAndSetUserTokenCookie(userTokenId, response);
                    return MY_APP_TYPE;
                } else {
                    logger.trace("Got user from userticket - Got no valid user, retrying login");
                    CookieManager.removeUserTokenCookies(request, response);
                    return LOGIN_SERVICE;
                }
            }
        } catch (MissingResourceException mre) {
            logger.trace("myapp - The ticked might have already been used, checking the cookie.");
        }

        try {
            if (CookieManager.hasRightCookie(request)) {
                String userTokenIdFromCookie = CookieManager.getUserTokenIdFromCookie(request);
                if (userTokenIdFromCookie == null || userTokenIdFromCookie.length() < 7) {
                    CookieManager.removeUserTokenCookies(request, response);
                    return LOGIN_SERVICE;
                }
                logger.trace("myapp - userTokenIdFromCookie=" + userTokenIdFromCookie);
                String userTokenXml = tokenServiceClient.getUserTokenFromUserTokenId(userTokenIdFromCookie);
                logger.trace("myapp - userTokenXml=" + userTokenXml);

                if (userTokenXml.length() >= MIN_USER_TOKEN_LENGTH) {

                    addModelParams(model, userTokenIdFromCookie);
                    if (!UserTokenXpathHelper.hasUserAdminRight(userTokenXml)) {
                        CookieManager.removeUserTokenCookies(request, response);
                        return LOGIN_SERVICE;
                    }
                    String userTokenIdFromUserTokenXml = UserTokenXpathHelper.getUserTokenIdFromUserTokenXML(userTokenXml);
                    CookieManager.createAndSetUserTokenCookie(userTokenIdFromUserTokenXml, response);

                    return MY_APP_TYPE;
                } else {

                    // Remove cookie with invalid usertokenid
                    CookieManager.removeUserTokenCookies(request, response);
                    return LOGIN_SERVICE;
                }
            }
        } catch (RuntimeException mre) {
            CookieManager.removeUserTokenCookies(request, response);
            logger.info("The usertoken found in the cookie is not valid.");
            return LOGOUT_SERVICE;
        }
        CookieManager.removeUserTokenCookies(request, response);
        return LOGIN_SERVICE;
    }


    private void addModelParams(Model model, String userTokenID) {
        if (userTokenID != null && userTokenID.length() >= MIN_USERTOKEN_ID_LENGTH) {
            model.addAttribute("token", tokenServiceClient.getUserTokenFromUserTokenId(userTokenID));
            model.addAttribute("logOutUrl", properties.getProperty("logonservice") + "logoutaction?redirectURI=" + MY_APP_URI);
            model.addAttribute("realName", UserTokenXpathHelper.getRealName(tokenServiceClient.getUserTokenFromUserTokenId(userTokenID)));
        } else {
            model.addAttribute("token", "Unauthorized");
            model.addAttribute("logOutUrl", properties.getProperty("logonservice") + "logoutaction?redirectURI=" + MY_APP_URI);
            model.addAttribute("realName", "Unknown User");
        }

        String baseUrl = "/useradmin/" + tokenServiceClient.getMyAppTokenId() + "/" + tokenServiceClient.getMyUserTokenId() + "/";
        model.addAttribute("baseUrl", baseUrl);
    }


}

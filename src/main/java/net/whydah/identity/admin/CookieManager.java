package net.whydah.identity.admin;

import net.whydah.identity.admin.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CookieManager {
    private static final String USER_TOKEN_REFERENCE_NAME = "whydahusertoken_useradminwebapp";
    private static final Logger logger = LoggerFactory.getLogger(CookieManager.class);
    private static final int DEFAULT_COOKIE_MAX_AGE = 365 * 24 * 60 * 60;

    private static String cookiedomain = null;

    private CookieManager() {
    }

    static {
        try {
            cookiedomain = AppConfig.readProperties().getProperty("cookiedomain");
        } catch (IOException e) {
            logger.warn("AppConfig.readProperties failed. cookiedomain was set to {}", cookiedomain, e);
        }
    }


    public static void createAndSetUserTokenCookie(String userTokenId, Integer tokenRemainingLifetimeSeconds, HttpServletResponse response) {
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, userTokenId);
        updateCookie(cookie, userTokenId, tokenRemainingLifetimeSeconds, response);
    }

    public static void updateUserTokenCookie(String userTokenId, Integer tokenRemainingLifetimeSeconds, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getUserTokenCookie(request);
        updateCookie(cookie, userTokenId, tokenRemainingLifetimeSeconds, response);
    }

    private static void updateCookie(Cookie cookie, String cookieValue, Integer tokenRemainingLifetimeSeconds, HttpServletResponse response) {
        if (cookieValue != null) {
            cookie.setValue(cookieValue);
        }
        //Only name and value are sent back to the server from the browser. The other attributes are only used by the browser to determine of the cookie should be sent or not.
        //http://en.wikipedia.org/wiki/HTTP_cookie#Setting_a_cookie

        if (tokenRemainingLifetimeSeconds == null) {
            tokenRemainingLifetimeSeconds = DEFAULT_COOKIE_MAX_AGE;
        }
        cookie.setMaxAge(tokenRemainingLifetimeSeconds);

        if (cookiedomain != null && !cookiedomain.isEmpty()) {
            cookie.setDomain(cookiedomain);
        }
        cookie.setPath("/");
        cookie.setSecure(true);
        logger.debug("Created/updated cookie with name={}, value/userTokenId={}, domain={}, path={}, maxAge={}, secure={}",
                cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(), cookie.getMaxAge(), cookie.getSecure());
        response.addCookie(cookie);
    }

    public static void clearUserTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getUserTokenCookie(request);
        if (cookie != null) {
            logger.trace("Cleared cookie with name={}, value/userTokenId={}, domain={}, path={}, maxAge={}, secure={}",
                    cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(), cookie.getMaxAge(), cookie.getSecure());
            cookie.setValue("");
            cookie.setMaxAge(0);
            if (cookiedomain != null && !cookiedomain.isEmpty()) {
                cookie.setDomain(cookiedomain);
            }
            cookie.setPath("/");
            cookie.setSecure(true);
            response.addCookie(cookie);
        }
    }


    public static String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie userTokenCookie = getUserTokenCookie(request);
        if (userTokenCookie != null && userTokenCookie.getValue().length() > 7) {
            return userTokenCookie.getValue();
        }

        return (userTokenCookie != null ? userTokenCookie.getValue() : null);
    }

    private static Cookie getUserTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            logger.debug("getUserTokenCookie: cookie with name={}, value={}", cookie.getName(), cookie.getValue());
            if (USER_TOKEN_REFERENCE_NAME.equalsIgnoreCase(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}

package net.whydah.identity.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieManager {
    public static final String USER_TOKEN_REFERENCE_NAME = "whydahusertoken_sso";
    private static final Logger logger = LoggerFactory.getLogger(CookieManager.class);


    public static Cookie createUserTokenCookie(String userTokenXml) {
        String usertokenID = UserTokenXpathHelper.getUserTokenIdFromUserTokenXML(userTokenXml);
        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, usertokenID);
        //int maxAge = calculateTokenRemainingLifetime(userTokenXml);
        int maxAge = 365 * 24 * 60 * 60; //TODO Calculating TokenLife is hindered by XML with differing schemas

        cookie.setMaxAge(maxAge);
        cookie.setValue(usertokenID);
        cookie.setSecure(true);
        logger.trace("Created cookie with name=" + USER_TOKEN_REFERENCE_NAME + ", usertokenid=" + usertokenID + ", maxAge=" + maxAge);
        return cookie;
    }

    public static String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        logger.trace("getUserTokenIdFromCookie -  header: " + cookies);
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            logger.trace("Cookie Value: " + cookie.getName() + " PATH: " + cookie.getPath() + " Domain:" + cookie.getDomain());
            if (cookie.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                if (cookie.getValue().length() > 7) {
                    return cookie.getValue();
                }
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
    public static boolean hasRightCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        // logger.info("=============> header: " + cookies);
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            //logger.info("Cookie: " + cookie.getName());
            if (cookie.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                logger.trace("Found cookie, name={}  value={}", cookie.getName(), cookie.getValue());
                return true;
            }
        }
        return false;
    }

    /*
    public static  Cookie getUserTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        logger.debug("getUserTokenCookie - header: " + cookies);
        if (cookies == null) {
            return null;
        }

        for (Cookie cooky : cookies) {
            logger.debug("Cookie: " + cooky.getName());
            if (cooky.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                return cooky;
            }
        }
        return null;
    }
    */

    public static void removeUserTokenCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            for (Cookie cookie : cookies) {
                logger.debug("Cookie: " + cookie.getName());
                if (cookie.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                    cookie.setValue(USER_TOKEN_REFERENCE_NAME);
                    cookie.setMaxAge(0);
                    cookie.setPath("");
                    cookie.setValue("");
                    response.addCookie(cookie);
                }
            }
        }
    }

}

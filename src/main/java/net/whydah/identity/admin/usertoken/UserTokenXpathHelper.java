package net.whydah.identity.admin.usertoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class UserTokenXpathHelper {
    private static final Logger logger = LoggerFactory.getLogger(UserTokenXpathHelper.class);

    public static String getUserTokenIdFromUserTokenXML(String userTokenXml) {
        if (userTokenXml == null) {
            logger.trace("Empty  userToken");
            return "";
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/@id";
            XPathExpression xPathExpression = xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }


    public static  String getApplicationTokenIdFromAppTokenXML(String appTokenXML) {
        logger.trace("appTokenXML: {}", appTokenXML);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(appTokenXML)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/applicationtoken/params/applicationtokenID[1]";
            XPathExpression xPathExpression = xPath.compile(expression);
            String appId = xPathExpression.evaluate(doc);
            logger.trace("XML parse: applicationtokenID = {}", appId);
            return appId;
        } catch (Exception e) {
            logger.error("getAppTokenIdFromAppToken - Could not get applicationID from XML: " + appTokenXML, e);
        }
        return "";
    }

    public static String getRealName(String userTokenXml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/firstname[1]";
            XPathExpression xPathExpression =  xPath.compile(expression);
            String firstname = (xPathExpression.evaluate(doc));
            expression = "/usertoken/lastname[1]";
            xPathExpression = xPath.compile(expression);
            String lastname = (xPathExpression.evaluate(doc));
            return firstname + " " + lastname;
        } catch (Exception e) {
            logger.error("", e);
        }
        return "";
    }

    public static boolean hasUserAdminRight(String userTokenXml) {
        if (userTokenXml == null) {
            logger.trace("hasUserAdminRight - Empty  userToken");
            return false;
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/application[@ID=\"19\"]/role[@name=\"WhydahUserAdmin\"]/@value";
            XPathExpression xPathExpression = xPath.compile(expression);
            logger.trace("hasUserAdminRight - token" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc));
            String v = (xPathExpression.evaluate(doc));
            if (v == null || v.length() < 1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("getTimestamp - userTokenXml timestamp parsing error", e);
        }
        return false;
    }


    /*
    public static  String getLifespan(String userTokenXml) {
        if (userTokenXml == null){
            logger.trace("Empty  userToken");
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
            logger.error("getLifespan failed", e);
        }
        return "";
    }


    public static String getTimestamp(String userTokenXml) {
        if (userTokenXml==null){
            logger.trace("Empty  userToken");
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
            logger.error("getTimestamp error", e);
        }
        return "";
    }
    */
}

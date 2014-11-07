package net.whydah.identity.util;

import org.junit.Test;

public class XpathTest {


    @Test
    public void testRoleParsing() {
        String testToken = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<usertoken xmlns:ns2=\"http://www.w3.org/1999/xhtml\" id=\"4bd6ec15-f298-4cf8-9b46-d92775f6292c\">\n" +
                "    <uid>03124866-f094-42ca-9ff3-46d10fa4eff5</uid>\n" +
                "    <timestamp>1412079640658</timestamp>\n" +
                "    <lifespan>3600000</lifespan>\n" +
                "    <issuer></issuer>\n" +
                "    <securitylevel>1</securitylevel>\n" +
                "    <DEFCON>5</DEFCON>\n" +
                "    <username>totto@totto.org</username>\n" +
                "    <firstname>Thor Henning</firstname>\n" +
                "    <lastname>Hetland</lastname>\n" +
                "    <email>totto@totto.org</email>\n" +
                "    <personRef>22</personRef>\n" +
                "    <application ID=\"19\">\n" +
                "        <applicationName>UserAdminWebApplication</applicationName>\n" +
                "        <organizationName>Altran</organizationName>\n" +
                "        <role name=\"WhydahUserAdmin\" value=\"1\"/>\n" +
                "    </application>\n" +
                "    <application ID=\"100\">\n" +
                "        <applicationName>ACS</applicationName>\n" +
                "        <organizationName>Altran</organizationName>\n" +
                "        <role name=\"Employee\" value=\"totto@altran.com\"/>\n" +
                "    </application>\n" +
                "\n" +
                "    <ns2:link type=\"application/xml\" href=\"/4bd6ec15-f298-4cf8-9b46-d92775f6292c\" rel=\"self\"/>\n" +
                "    <hash type=\"MD5\">21d3f0edf36cb5a0486b592fe84621</hash>\n" +
                "</usertoken>";

        assert (TokenServiceClient.hasUserAdminRight(testToken));

    }
}

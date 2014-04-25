package net.whydah.identity.web;

import net.whydah.identity.config.AppConfig;
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
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by lho on 25.04.14.
 */
@RequestMapping("/{apptokenid}/{usertokenid}")
@Controller
public class UserAdminUibController {
    private static final Logger logger = LoggerFactory.getLogger(UserAdminUibController.class);
    private final String UIB_URL;
    private final HttpClient httpClient;

    public UserAdminUibController() throws IOException {
        Properties properties = AppConfig.readProperties();
        UIB_URL = properties.getProperty("useridentitybackend");
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RequestMapping(value = "/users/find/{query}", method = RequestMethod.GET)
    public String findUsers(@PathVariable("apptokenid") String apptokenid, @PathVariable("usertokenid") String usertokenid, @PathVariable("query") String query, HttpServletRequest request, HttpServletResponse response, Model model) {
        logger.trace("Finding users with query: " + query);
        HttpMethod method = new GetMethod();
        String url = UIB_URL + apptokenid + "/" + usertokenid +"/users/find/"+query;
        makeUibRequest(method, url, model, response);
        return "json";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RequestMapping(value = "/user/{uid}/", method = RequestMethod.GET)
    public String getUser(@PathVariable("apptokenid") String apptokenid, @PathVariable("usertokenid") String usertokenid, @PathVariable("uid") String uid, HttpServletRequest request, HttpServletResponse response, Model model) {
        logger.trace("Getting user with uid: " + uid);
        HttpMethod method = new GetMethod();
        String url = UIB_URL + apptokenid + "/" + usertokenid +"/user/"+uid;
        makeUibRequest(method, url, model, response);
        return "json";
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RequestMapping(value = "/user/{uid}/", method = RequestMethod.PUT)
    public String putUser(@PathVariable("apptokenid") String apptokenid, @PathVariable("usertokenid") String usertokenid, @PathVariable("uid") String uid, HttpServletRequest request, HttpServletResponse response, Model model) {
        logger.trace("Putting user with uid: " + uid);
        PutMethod method = new PutMethod();
        InputStreamRequestEntity inputStreamRequestEntity = null;
        try {
            inputStreamRequestEntity = new InputStreamRequestEntity(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        method.setRequestEntity(inputStreamRequestEntity);
        String url = getUibUrl(apptokenid, usertokenid, "user/" + uid);
        makeUibRequest(method, url, model, response);
        return "json";
    }

    private String getUibUrl(String apptokenid, String usertokenid, String s) {
        return UIB_URL + apptokenid + "/" + usertokenid + "/" + s;
    }

    private void makeUibRequest(HttpMethod method, String url, Model model, HttpServletResponse response) {
        HttpMethodParams params = new HttpMethodParams();
        params.setHttpElementCharset("UTF-8");
        params.setContentCharset("UTF-8");
        method.setParams(params);
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
            response.setStatus(rescode);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            method.releaseConnection();
        }
        if (!model.containsAttribute("jsondata")) {
            logger.error("jsondata attribute not set when fetching data from URL: {}", url);
        }
    }


}
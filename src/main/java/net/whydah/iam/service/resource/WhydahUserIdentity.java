package net.whydah.iam.service.resource;

import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Controller
@Path("/iam")
public class WhydahUserIdentity {


    @Context
    UriInfo uriInfo;

    @Path("/customers/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(@PathParam("applicationtokenid") String applicationtokenid,
                                 @FormParam("apptoken") String appTokenXml,
                                 @FormParam("usercredential") String userCredentialXml) {

        String jsonres = "{\"customers\":\n" +
                "  {\"@uri\":\"http://host/dojoRest/resources/customers/\",\n" +
                "   \"customer\":[\n" +
                "     {\"@uri\":\"http://host/dojoRest/resources/customers/1/\",\n" +
                "       \"name\":\"JumboCom\",\n" +
                "      \"city\":\"Fort Lauderdale\",     \n" +
                "       \"state\":\"FL\",\n" +
                "       \"zip\":\"33015\"},\n" +
                "     {\"@uri\":\"http://host/dojoRest/resources/customers/2/\",\n" +
                "       \"name\":\"Livermore Enterprises\",\n" +
                "       \"city\":\"Miami\",\n" +
                "       \"state\":\"FL\",\n" +
                "       \"zip\":\"33055\"}\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        return Response.ok().language(jsonres).build();
    }


}

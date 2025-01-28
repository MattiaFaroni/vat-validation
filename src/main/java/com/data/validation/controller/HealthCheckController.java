package com.data.validation.controller;

import com.data.validation.model.wrapper.VatCheckRequest;
import com.data.validation.model.wrapper.VatCheckResponse;
import com.data.validation.service.VatCheckService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Path("/healthcheck")
public class HealthCheckController {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() throws IOException {

        VatCheckResponse vatCheckResponse = callVatCheckService();

        InputStream in = getClass().getClassLoader().getResourceAsStream("../build.properties");
        Properties props = new Properties();
        props.load(in);
        String version = props.get("projectVersion").toString();

        File TomcatRun = new File(System.getProperty("catalina.home") + "/" + "TomcatRun.info");

        if (vatCheckResponse.getData() != null && vatCheckResponse.getData().getValid() && TomcatRun.exists()) {
            return Response.status(Response.Status.OK).entity(String.format("API version n° %s is running.", version)).build();
        } else {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }

    private VatCheckResponse callVatCheckService() {
        VatCheckRequest vatCheckRequest = new VatCheckRequest("IT", "00159560366");
        VatCheckService vatCheckService = new VatCheckService();
        return vatCheckService.vatCheck(vatCheckRequest);
    }
}

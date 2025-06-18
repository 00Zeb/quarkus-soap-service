package org.example.rest.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.example.rest.dto.HelloRequest;
import org.example.rest.dto.HelloResponse;

/**
 * REST Client interface for Hello World service
 * Mirrors the SOAP client functionality but uses REST/JSON
 */
@RegisterRestClient(configKey = "hello-world-rest-client")
@Path("/api/hello")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface HelloWorldRestClient {

    /**
     * Simple hello world method - GET endpoint
     * @param name the name to greet (path parameter)
     * @return greeting message
     */
    @GET
    @Path("/say/{name}")
    HelloResponse sayHelloGet(@PathParam("name") String name);

    /**
     * Simple hello world method - POST endpoint
     * @param request the request containing the name
     * @return greeting message
     */
    @POST
    @Path("/say")
    HelloResponse sayHelloPost(HelloRequest request);

    /**
     * Get current server time
     * @return current timestamp
     */
    @GET
    @Path("/time")
    HelloResponse getServerTime();

    /**
     * Echo method for testing - GET endpoint
     * @param message the message to echo (query parameter)
     * @return the same message
     */
    @GET
    @Path("/echo")
    HelloResponse echoGet(@QueryParam("message") String message);

    /**
     * Echo method for testing - POST endpoint
     * @param request the request containing the message
     * @return the same message
     */
    @POST
    @Path("/echo")
    HelloResponse echoPost(HelloRequest request);
}

package org.example.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.rest.dto.HelloRequest;
import org.example.rest.dto.HelloResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * REST Hello World Service
 * Mirrors the SOAP HelloWorldService functionality but uses REST/JSON
 */
@Path("/api/hello")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HelloWorldRestService {

    /**
     * Simple hello world method - GET endpoint
     * @param name the name to greet (path parameter)
     * @return greeting message
     */
    @GET
    @Path("/say/{name}")
    public Response sayHelloGet(@PathParam("name") String name) {
        String result = generateHelloMessage(name);
        HelloResponse response = new HelloResponse(result);
        return Response.ok(response).build();
    }

    /**
     * Simple hello world method - POST endpoint
     * @param request the request containing the name
     * @return greeting message
     */
    @POST
    @Path("/say")
    public Response sayHelloPost(HelloRequest request) {
        String name = request != null ? request.getName() : null;
        String result = generateHelloMessage(name);
        HelloResponse response = new HelloResponse(result);
        return Response.ok(response).build();
    }

    /**
     * Get current server time
     * @return current timestamp
     */
    @GET
    @Path("/time")
    public Response getServerTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeString = "Current server time: " + now.format(formatter);
        
        HelloResponse response = new HelloResponse(timeString, now.format(formatter));
        return Response.ok(response).build();
    }

    /**
     * Echo method for testing - GET endpoint
     * @param message the message to echo (query parameter)
     * @return the same message
     */
    @GET
    @Path("/echo")
    public Response echoGet(@QueryParam("message") String message) {
        String result = generateEchoMessage(message);
        HelloResponse response = new HelloResponse(result);
        return Response.ok(response).build();
    }

    /**
     * Echo method for testing - POST endpoint
     * @param request the request containing the message
     * @return the same message
     */
    @POST
    @Path("/echo")
    public Response echoPost(HelloRequest request) {
        String message = request != null ? request.getMessage() : null;
        String result = generateEchoMessage(message);
        HelloResponse response = new HelloResponse(result);
        return Response.ok(response).build();
    }

    private String generateHelloMessage(String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "World";
        }
        return "Hello, " + name + "! Welcome to Quarkus REST Service with Mutual TLS!";
    }

    private String generateEchoMessage(String message) {
        if (message == null) {
            return "Echo: null";
        }
        return "Echo: " + message;
    }
}

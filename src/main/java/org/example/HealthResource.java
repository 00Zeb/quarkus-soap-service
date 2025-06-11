package org.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Simple REST endpoint for health checks and service information
 */
@Path("/health")
public class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        Map<String, Object> status = Map.of(
            "status", "UP",
            "service", "Quarkus SOAP Service",
            "timestamp", LocalDateTime.now().toString(),
            "soap_endpoint", "/soap",
            "wsdl_url", "/soap/HelloWorldService?wsdl",
            "https_enabled", true
        );
        return Response.ok(status).build();
    }

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        return """
            🚀 Quarkus SOAP Service with HTTPS
            
            📡 SOAP Endpoint: https://localhost:8443/soap
            📋 WSDL: https://localhost:8443/soap/HelloWorldService?wsdl
            🔒 HTTPS: Enabled with self-signed certificate
            ⚡ Health Check: https://localhost:8443/health
            
            Available SOAP Methods:
            - sayHello(name): Returns a greeting message
            - getServerTime(): Returns current server time
            - echo(message): Echoes the input message
            """;
    }
}

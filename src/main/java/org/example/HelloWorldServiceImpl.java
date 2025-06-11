package org.example;

import io.quarkiverse.cxf.annotation.CXFEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jws.WebService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of the Hello World SOAP service
 */
@WebService(
    serviceName = "HelloWorldService",
    portName = "HelloWorldPort",
    targetNamespace = "http://example.org/",
    endpointInterface = "org.example.HelloWorldService"
)
@CXFEndpoint("/HelloWorldService")
@ApplicationScoped
public class HelloWorldServiceImpl implements HelloWorldService {

    @Override
    public String sayHello(String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "World";
        }
        return "Hello, " + name + "! Welcome to Quarkus SOAP Service with HTTPS!";
    }

    @Override
    public String getServerTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Current server time: " + now.format(formatter);
    }

    @Override
    public String echo(String message) {
        if (message == null) {
            return "Echo: null";
        }
        return "Echo: " + message;
    }
}

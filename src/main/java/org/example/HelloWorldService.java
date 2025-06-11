package org.example;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * SOAP Web Service interface for Hello World service
 */
@WebService(name = "HelloWorldService", targetNamespace = "http://example.org/")
public interface HelloWorldService {

    /**
     * Simple hello world method
     * @param name the name to greet
     * @return greeting message
     */
    @WebMethod
    String sayHello(@WebParam(name = "name") String name);

    /**
     * Get current server time
     * @return current timestamp
     */
    @WebMethod
    String getServerTime();

    /**
     * Echo method for testing
     * @param message the message to echo
     * @return the same message
     */
    @WebMethod
    String echo(@WebParam(name = "message") String message);
}

package org.example;

import io.quarkiverse.cxf.annotation.CXFEndpoint;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * CXF Configuration for SOAP services
 */
@ApplicationScoped
public class CxfConfig {

    @CXFEndpoint("/HelloWorldService")
    HelloWorldService helloWorldService() {
        return new HelloWorldServiceImpl();
    }
}

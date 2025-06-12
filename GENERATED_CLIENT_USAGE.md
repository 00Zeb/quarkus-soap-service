# Generated SOAP Client Usage Guide

This document explains how to use the generated SOAP client code from the WSDL.

## Overview

The project now includes automatic SOAP client code generation from WSDL using the Apache CXF Maven plugin. The generated client classes are created in the `target/generated-sources/cxf/org/example/client/` directory.

## Generated Client Classes

When you run `./mvnw clean generate-sources`, the following client classes are generated:

- **`HelloWorldService.java`** - The client interface with all service methods
- **`HelloWorldService_Service.java`** - The service factory class
- **`SayHello.java`, `SayHelloResponse.java`** - Request/response wrapper classes for sayHello method
- **`GetServerTime.java`, `GetServerTimeResponse.java`** - Request/response wrapper classes for getServerTime method
- **`Echo.java`, `EchoResponse.java`** - Request/response wrapper classes for echo method
- **`ObjectFactory.java`** - JAXB object factory for creating instances
- **`package-info.java`** - Package information

## How to Use the Generated Client

### 1. Generate the Client Code

```bash
# Generate client code from WSDL
./mvnw clean generate-sources

# Compile the project (includes generated sources)
./mvnw compile
```

### 2. Basic Usage Example

```java
import org.example.client.HelloWorldService;
import org.example.client.HelloWorldService_Service;
import jakarta.xml.ws.BindingProvider;

// Create the service using generated client code
HelloWorldService_Service service = new HelloWorldService_Service();
HelloWorldService port = service.getHelloWorldPort();

// Configure the endpoint URL
BindingProvider bindingProvider = (BindingProvider) port;
bindingProvider.getRequestContext().put(
    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
    "https://localhost:8444/soap/HelloWorldService"
);

// Call service methods
String response1 = port.sayHello("Your Name");
String response2 = port.getServerTime();
String response3 = port.echo("Test message");
```

### 3. With Mutual TLS Configuration

For production use with mutual TLS, you'll need to configure SSL context:

```java
// Configure SSL with client certificates (see GeneratedSoapClientTest.java for full example)
configureMutualTLS();

// Then use the client as shown above
HelloWorldService_Service service = new HelloWorldService_Service();
HelloWorldService port = service.getHelloWorldPort();
// ... rest of the code
```

## Test Examples

The project includes two test classes demonstrating different approaches:

1. **`SoapClientTest.java`** - Uses Quarkus CXF client with automatic configuration via `application.properties`
2. **`GeneratedSoapClientTest.java`** - Uses the generated client classes directly with manual SSL configuration

## Maven Plugin Configuration

The client generation is configured in `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-codegen-plugin</artifactId>
    <version>4.0.3</version>
    <executions>
        <execution>
            <id>generate-sources</id>
            <phase>generate-sources</phase>
            <configuration>
                <sourceRoot>${project.build.directory}/generated-sources/cxf</sourceRoot>
                <wsdlOptions>
                    <wsdlOption>
                        <wsdl>${basedir}/src/main/resources/HelloWorldService.wsdl</wsdl>
                        <packagenames>
                            <packagename>org.example.client</packagename>
                        </packagenames>
                    </wsdlOption>
                </wsdlOptions>
            </configuration>
            <goals>
                <goal>wsdl2java</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## WSDL Files

The WSDL and XSD files are located in `src/main/resources/`:
- `HelloWorldService.wsdl` - Main WSDL file
- `HelloWorldService_schema1.xsd` - XML Schema definitions

## Benefits of Generated Client Code

1. **Type Safety** - Strongly typed Java classes for all operations
2. **IDE Support** - Full IntelliSense/autocomplete support
3. **Compile-time Validation** - Catch errors at compile time
4. **Documentation** - Generated classes include JavaDoc comments
5. **Maintainability** - Automatically updated when WSDL changes

## Running Tests

```bash
# Run all tests (including client tests)
./mvnw test

# Run specific test class
./mvnw test -Dtest=GeneratedSoapClientTest

# Enable specific test methods by removing @Disabled annotation
```

## Notes

- The generated client code is created during the `generate-sources` Maven phase
- Generated classes are in the `target/generated-sources/cxf/` directory
- The client code is automatically included in the compilation classpath
- For mutual TLS, ensure client certificates are properly configured
- The WSDL file can be updated and regenerated as needed

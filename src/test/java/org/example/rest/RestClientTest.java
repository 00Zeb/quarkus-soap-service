package org.example.rest;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.example.rest.client.HelloWorldRestClient;
import org.example.rest.dto.HelloRequest;
import org.example.rest.dto.HelloResponse;
import org.junit.jupiter.api.Test;

/**
 * Test class for REST Hello World client with mutual TLS
 * Mirrors the SOAP client test functionality but uses REST/JSON
 */
@QuarkusTest
public class RestClientTest {

    @RestClient
    HelloWorldRestClient helloWorldRestClient;

    @ConfigProperty(name = "rest.service.url")
    String serviceUrl;

    @Test
    //@Disabled("Enable this test when you want to test the REST service manually")
    public void testRestHelloWorldService() throws Exception {
        System.out.println("=== Testing REST Hello World Service ===");
        System.out.println("Service URL: " + serviceUrl);
        System.out.println("REST client class: " + helloWorldRestClient.getClass().getName());
        System.out.println("");

        // Test sayHello GET endpoint
        System.out.println("1. Testing sayHello GET endpoint...");
        HelloResponse response1 = helloWorldRestClient.sayHelloGet("John");
        System.out.println("Response: " + response1);
        assert response1 != null;
        assert response1.getResult().contains("Hello, John");
        assert "SUCCESS".equals(response1.getStatus());
        System.out.println("✅ sayHello GET test passed");
        System.out.println("");

        // Test sayHello POST endpoint
        System.out.println("2. Testing sayHello POST endpoint...");
        HelloRequest request = new HelloRequest("Jane");
        HelloResponse response2 = helloWorldRestClient.sayHelloPost(request);
        System.out.println("Response: " + response2);
        assert response2 != null;
        assert response2.getResult().contains("Hello, Jane");
        assert "SUCCESS".equals(response2.getStatus());
        System.out.println("✅ sayHello POST test passed");
        System.out.println("");

        // Test getServerTime
        System.out.println("3. Testing getServerTime...");
        HelloResponse response3 = helloWorldRestClient.getServerTime();
        System.out.println("Response: " + response3);
        assert response3 != null;
        assert response3.getResult().contains("Current server time");
        assert response3.getTimestamp() != null;
        assert "SUCCESS".equals(response3.getStatus());
        System.out.println("✅ getServerTime test passed");
        System.out.println("");

        // Test echo GET endpoint
        System.out.println("4. Testing echo GET endpoint...");
        HelloResponse response4 = helloWorldRestClient.echoGet("Test message");
        System.out.println("Response: " + response4);
        assert response4 != null;
        assert response4.getResult().equals("Echo: Test message");
        assert "SUCCESS".equals(response4.getStatus());
        System.out.println("✅ echo GET test passed");
        System.out.println("");

        // Test echo POST endpoint
        System.out.println("5. Testing echo POST endpoint...");
        HelloRequest echoRequest = new HelloRequest();
        echoRequest.setMessage("POST test message");
        HelloResponse response5 = helloWorldRestClient.echoPost(echoRequest);
        System.out.println("Response: " + response5);
        assert response5 != null;
        assert response5.getResult().equals("Echo: POST test message");
        assert "SUCCESS".equals(response5.getStatus());
        System.out.println("✅ echo POST test passed");
        System.out.println("");

        System.out.println("🎉 All REST client tests passed!");
    }
}

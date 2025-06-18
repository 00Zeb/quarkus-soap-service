package org.example.rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.example.rest.client.HelloWorldRestClient;
import org.example.rest.dto.HelloRequest;
import org.example.rest.dto.HelloResponse;
import org.junit.jupiter.api.*;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Test class using WireMock to mock the REST Hello World service
 * 
 * This test uses pre-created mock mappings to provide fast, reliable tests 
 * without depending on the external service.
 */
@QuarkusTest
@TestProfile(RestClientMockedTest.MockedTestProfile.class)
public class RestClientMockedTest {

    private static WireMockServer wireMockServer;
    
    @RestClient
    HelloWorldRestClient helloWorldRestClient;

    @ConfigProperty(name = "rest.service.url")
    String serviceUrl;

    /**
     * Test profile to override the service URL to point to WireMock
     */
    public static class MockedTestProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                // Point to WireMock instead of real service
                "rest.service.url", "http://localhost:8090/api/hello",
                "quarkus.rest-client.hello-world-rest-client.url", "http://localhost:8090",
                // Disable TLS for mocking
                "quarkus.rest-client.hello-world-rest-client.tls-configuration-name", ""
            );
        }
    }

    @BeforeAll
    static void setupWireMock() {
        // Start WireMock server
        wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                .port(8090) // HTTP port (matches our test profile)
        );
        
        wireMockServer.start();
        WireMock.configureFor("localhost", 8090);
        
        System.out.println("WireMock server started for REST mocking on port 8090");
    }

    @AfterAll
    static void tearDownWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            System.out.println("WireMock server stopped");
        }
    }

    @BeforeEach
    void setupMocks() {
        // Reset WireMock before each test
        wireMockServer.resetAll();
        
        // Setup all the mocks
        setupSuccessfulMocks();
        
        System.out.println("WireMock reset and mocks configured for REST test");
    }

    @Test
    public void testRestServiceWithMocks() throws Exception {
        System.out.println("=== Testing with WireMock Mocked REST Service ===");
        System.out.println("Service URL: " + serviceUrl);
        
        // Test sayHello GET endpoint
        System.out.println("1. Testing sayHello GET endpoint with mock...");
        HelloResponse response1 = helloWorldRestClient.sayHelloGet("TestUser");
        System.out.println("Response: " + response1);
        assert response1 != null;
        assert response1.getResult().contains("Hello, TestUser");
        assert "SUCCESS".equals(response1.getStatus());
        System.out.println("✅ sayHello GET mock test passed");
        
        // Test sayHello POST endpoint
        System.out.println("2. Testing sayHello POST endpoint with mock...");
        HelloRequest request = new HelloRequest("MockUser");
        HelloResponse response2 = helloWorldRestClient.sayHelloPost(request);
        System.out.println("Response: " + response2);
        assert response2 != null;
        assert response2.getResult().contains("Hello, MockUser");
        assert "SUCCESS".equals(response2.getStatus());
        System.out.println("✅ sayHello POST mock test passed");
        
        // Test getServerTime
        System.out.println("3. Testing getServerTime with mock...");
        HelloResponse response3 = helloWorldRestClient.getServerTime();
        System.out.println("Response: " + response3);
        assert response3 != null;
        assert response3.getResult().contains("Current server time");
        assert "SUCCESS".equals(response3.getStatus());
        System.out.println("✅ getServerTime mock test passed");
        
        // Test echo GET endpoint
        System.out.println("4. Testing echo GET endpoint with mock...");
        HelloResponse response4 = helloWorldRestClient.echoGet("Mock message");
        System.out.println("Response: " + response4);
        assert response4 != null;
        assert response4.getResult().equals("Echo: Mock message");
        assert "SUCCESS".equals(response4.getStatus());
        System.out.println("✅ echo GET mock test passed");
        
        System.out.println("🎉 All REST mock tests passed!");
    }

    private void setupSuccessfulMocks() {
        // Mock sayHello GET endpoint
        wireMockServer.stubFor(get(urlMatching("/say/.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Hello, TestUser! Welcome to Quarkus REST Service with Mutual TLS!",
                        "status": "SUCCESS"
                    }
                    """)));

        // Mock sayHello POST endpoint
        wireMockServer.stubFor(post(urlEqualTo("/say"))
            .withHeader("Content-Type", containing("application/json"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Hello, MockUser! Welcome to Quarkus REST Service with Mutual TLS!",
                        "status": "SUCCESS"
                    }
                    """)));

        // Mock getServerTime endpoint
        wireMockServer.stubFor(get(urlEqualTo("/time"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Current server time: 2024-01-15 10:30:45",
                        "timestamp": "2024-01-15 10:30:45",
                        "status": "SUCCESS"
                    }
                    """)));

        // Mock echo GET endpoint
        wireMockServer.stubFor(get(urlMatching("/echo.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Echo: Mock message",
                        "status": "SUCCESS"
                    }
                    """)));

        System.out.println("Mock responses configured for REST endpoints");
    }
}

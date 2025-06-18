package org.example.rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.example.rest.client.HelloWorldRestClient;
import org.example.rest.dto.HelloResponse;
import org.junit.jupiter.api.*;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Test to verify that the @Path annotation in the REST client interface
 * correctly affects the URL path construction
 */
@QuarkusTest
@TestProfile(RestClientPathTest.PathTestProfile.class)
public class RestClientPathTest {

    private static WireMockServer wireMockServer;
    
    @RestClient
    HelloWorldRestClient helloWorldRestClient;

    /**
     * Test profile to point to WireMock and verify path construction
     */
    public static class PathTestProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                // Point to WireMock base URL WITHOUT /api/hello
                "quarkus.rest-client.hello-world-rest-client.url", "http://localhost:8092",
                // Disable TLS for this test
                "quarkus.rest-client.hello-world-rest-client.tls-configuration-name", ""
            );
        }
    }

    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(
            WireMockConfiguration.options().port(8092)
        );
        wireMockServer.start();
        WireMock.configureFor("localhost", 8092);
        System.out.println("WireMock server started on port 8092 for path testing");
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
        wireMockServer.resetAll();
    }

    @Test
    public void testPathAnnotationInClientInterface() {
        System.out.println("=== Testing @Path annotation effect on URL construction ===");
        
        // Setup mock for the FULL path including /api/hello from @Path annotation
        wireMockServer.stubFor(get(urlEqualTo("/api/hello/say/PathTest"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Hello, PathTest! Path annotation working correctly!",
                        "status": "SUCCESS"
                    }
                    """)));

        // This should call: http://localhost:8092/api/hello/say/PathTest
        // Because: base URL (http://localhost:8092) + @Path("/api/hello") + method @Path("/say/{name}")
        HelloResponse response = helloWorldRestClient.sayHelloGet("PathTest");
        
        System.out.println("Response: " + response);
        assert response != null;
        assert response.getResult().contains("Path annotation working correctly!");
        assert "SUCCESS".equals(response.getStatus());
        
        // Verify the request was made to the correct path
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/hello/say/PathTest")));
        
        System.out.println("✅ @Path annotation correctly adds /api/hello to the URL path");
    }

    @Test
    public void testPathAnnotationAffectsActualUrls() {
        System.out.println("=== Testing that @Path annotation affects actual URLs called ===");

        // Setup mocks for both paths to see which one gets called
        wireMockServer.stubFor(get(urlEqualTo("/say/UrlTest"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Called WITHOUT /api/hello prefix",
                        "status": "SUCCESS"
                    }
                    """)));

        wireMockServer.stubFor(get(urlEqualTo("/api/hello/say/UrlTest"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Called WITH /api/hello prefix",
                        "status": "SUCCESS"
                    }
                    """)));

        // Make the call
        HelloResponse response = helloWorldRestClient.sayHelloGet("UrlTest");

        System.out.println("Response: " + response);

        // Check which path was actually called
        try {
            wireMockServer.verify(1, getRequestedFor(urlEqualTo("/api/hello/say/UrlTest")));
            wireMockServer.verify(0, getRequestedFor(urlEqualTo("/say/UrlTest")));

            System.out.println("✅ Client called /api/hello/say/UrlTest (WITH @Path prefix)");
            assert response.getResult().contains("WITH /api/hello prefix");

        } catch (Exception e) {
            // Check if it called the path without prefix
            try {
                wireMockServer.verify(1, getRequestedFor(urlEqualTo("/say/UrlTest")));
                wireMockServer.verify(0, getRequestedFor(urlEqualTo("/api/hello/say/UrlTest")));

                System.out.println("❌ Client called /say/UrlTest (WITHOUT @Path prefix)");
                assert response.getResult().contains("WITHOUT /api/hello prefix");
                assert false : "@Path annotation is not working - client called wrong URL";

            } catch (Exception e2) {
                assert false : "Could not determine which URL was called: " + e.getMessage();
            }
        }
    }

    @Test
    public void testAllEndpointsUsePath() {
        System.out.println("=== Testing that all endpoints use the @Path prefix ===");
        
        // Setup mocks for all endpoints with /api/hello prefix
        wireMockServer.stubFor(get(urlEqualTo("/api/hello/time"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\": \"Time endpoint with path\", \"status\": \"SUCCESS\"}")));

        wireMockServer.stubFor(get(urlMatching("/api/hello/echo.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\": \"Echo endpoint with path\", \"status\": \"SUCCESS\"}")));

        // Test time endpoint
        HelloResponse timeResponse = helloWorldRestClient.getServerTime();
        assert timeResponse.getResult().contains("Time endpoint with path");
        
        // Test echo endpoint
        HelloResponse echoResponse = helloWorldRestClient.echoGet("test");
        assert echoResponse.getResult().contains("Echo endpoint with path");
        
        // Verify correct paths were called
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/hello/time")));
        wireMockServer.verify(getRequestedFor(urlMatching("/api/hello/echo.*")));
        
        System.out.println("✅ All endpoints correctly use the @Path(\"/api/hello\") prefix");
    }
}

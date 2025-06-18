package org.example.rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.recording.RecordingStatus;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.example.rest.client.HelloWorldRestClient;
import org.example.rest.dto.HelloRequest;
import org.example.rest.dto.HelloResponse;
import org.junit.jupiter.api.*;

/**
 * Test to capture real REST service responses and generate WireMock stubs
 * This test records interactions with the real service to create mappings
 */
@QuarkusTest
public class RestMockGeneratorTest {

    private static WireMockServer wireMockServer;

    @RestClient
    HelloWorldRestClient helloWorldRestClient;

    @ConfigProperty(name = "rest.service.url")
    String serviceUrl;

    @BeforeAll
    static void setupWireMock() {
        // Configure WireMock with HTTP only for recording (avoid certificate issues)
        wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                .port(8091) // WireMock proxy port for REST
                .enableBrowserProxying(true) // Enable proxy mode
                .trustAllProxyTargets(true) // Trust all SSL certificates
                .preserveHostHeader(true) // Preserve original host header
        );
        
        wireMockServer.start();
        
        // Configure WireMock to record interactions with the real service
        WireMock.configureFor("localhost", 8091);
        
        System.out.println("WireMock server started on port 8091 (HTTP) for REST recording");
        System.out.println("Recording mode enabled - will proxy to real REST service");
    }

    @AfterAll
    static void tearDownWireMock() {
        if (wireMockServer != null) {
            // Stop recording and save mappings
            if (wireMockServer.getRecordingStatus().getStatus() == RecordingStatus.Recording) {
                wireMockServer.stopRecording();
                System.out.println("Recording stopped and mappings saved");
            }
            
            wireMockServer.stop();
            System.out.println("WireMock server stopped");
        }
    }

    @BeforeEach
    void setupRecording() {
        // Reset any existing mappings
        wireMockServer.resetAll();
        
        // Start recording to the real service
        // This will capture all requests and responses
        wireMockServer.startRecording("https://localhost:8444");
        
        System.out.println("Started recording REST interactions to: https://localhost:8444");
    }

    @AfterEach
    void stopRecording() {
        // Stop recording after each test
        if (wireMockServer.getRecordingStatus().getStatus() == RecordingStatus.Recording) {
            wireMockServer.stopRecording();
            System.out.println("Recording stopped for this test");
        }
    }

    @Test
    @Disabled("Enable this test when you want to record real REST service interactions")
    public void recordRestServiceInteractions() throws Exception {
        System.out.println("=== Recording REST Service Interactions ===");
        System.out.println("Target service URL: " + serviceUrl);
        System.out.println("This test will record real service calls to generate WireMock mappings");
        System.out.println("");

        try {
            // Record sayHello GET call
            System.out.println("Recording sayHello GET call...");
            HelloResponse response1 = helloWorldRestClient.sayHelloGet("RecordingUser");
            System.out.println("Recorded response: " + response1);

            // Record sayHello POST call
            System.out.println("Recording sayHello POST call...");
            HelloRequest request = new HelloRequest("PostUser");
            HelloResponse response2 = helloWorldRestClient.sayHelloPost(request);
            System.out.println("Recorded response: " + response2);

            // Record getServerTime call
            System.out.println("Recording getServerTime call...");
            HelloResponse response3 = helloWorldRestClient.getServerTime();
            System.out.println("Recorded response: " + response3);

            // Record echo GET call
            System.out.println("Recording echo GET call...");
            HelloResponse response4 = helloWorldRestClient.echoGet("Recording test message");
            System.out.println("Recorded response: " + response4);

            // Record echo POST call
            System.out.println("Recording echo POST call...");
            HelloRequest echoRequest = new HelloRequest();
            echoRequest.setMessage("POST recording message");
            HelloResponse response5 = helloWorldRestClient.echoPost(echoRequest);
            System.out.println("Recorded response: " + response5);

            System.out.println("");
            System.out.println("✅ All REST service interactions recorded successfully!");
            System.out.println("📁 Check the mappings directory for generated WireMock stubs");

        } catch (Exception e) {
            System.err.println("❌ Error during recording: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @Disabled("Enable this test to generate specific mock mappings manually")
    public void generateSpecificMockMappings() {
        System.out.println("=== Generating Specific REST Mock Mappings ===");
        
        // This test can be used to create specific mappings programmatically
        // instead of recording from a real service
        
        // Example: Create a mapping for sayHello GET endpoint
        wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/api/hello/say/.*"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "result": "Hello, {{request.pathSegments.[3]}}! Welcome to Quarkus REST Service with Mutual TLS!",
                        "status": "SUCCESS"
                    }
                    """)));
        
        System.out.println("✅ Specific mock mappings generated");
        System.out.println("💡 You can extend this method to create more specific mappings");
    }
}

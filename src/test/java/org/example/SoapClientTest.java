package org.example;

import io.quarkiverse.cxf.annotation.CXFClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import jakarta.inject.Inject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * Test class demonstrating how to call the SOAP service with mutual TLS
 *
 * Prerequisites:
 * 1. Run generate-mtls-certificates.bat to create client certificates
 * 2. Copy client-keystore.p12 to src/test/resources/ (or run copy-client-cert-for-tests.bat)
 * 3. Start the Quarkus application with mutual TLS enabled
 *
 * This test uses Quarkus CXF client configuration from application.properties
 * for automatic mutual TLS setup.
 */
@QuarkusTest
public class SoapClientTest {

    // Inject the SOAP client configured via application.properties (using generated client)
    @CXFClient("helloWorldClient")
    org.example.client.HelloWorldService helloWorldService;

    // Inject the service URL from application.properties
    @ConfigProperty(name = "soap.service.url")
    String serviceUrl;

    // Also inject the CXF client endpoint URL to show it's the same
    @ConfigProperty(name = "quarkus.cxf.client.helloWorldClient.client-endpoint-url")
    String cxfClientUrl;

    @BeforeEach
    void setupSSL() {
        // Set system properties to disable SSL verification for testing
        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/truststore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

        // Disable hostname verification for localhost
        System.setProperty("com.sun.net.ssl.checkRevocation", "false");
        System.setProperty("javax.net.ssl.trustStoreProvider", "SUN");

        // Alternative: disable all SSL verification (for testing only!)
        System.setProperty("javax.net.ssl.trustStore", "");
        System.setProperty("javax.net.ssl.trustStorePassword", "");

        System.out.println("SSL system properties configured for testing");
    }


    /**
     * Test using Quarkus CXF client with automatic mutual TLS configuration
     * This uses the generated client interface and URL from application.properties
     */
    @Test
    // @Disabled("Enable this test when you want to test the SOAP service manually")
    public void testSoapServiceWithQuarkusClient() throws Exception {
        System.out.println("=== Configuration from application.properties ===");
        System.out.println("Custom service URL: " + serviceUrl);
        System.out.println("CXF client URL: " + cxfClientUrl);
        System.out.println("Generated client class: " + helloWorldService.getClass().getName());
        System.out.println("URLs match: " + serviceUrl.equals(cxfClientUrl));

        // Call the service using the injected client (mutual TLS configured automatically)
        String response = helloWorldService.sayHello("Quarkus with mTLS from Config");

        System.out.println("SOAP Response: " + response);

        // Assertions
        assert response != null;
        assert response.contains("Hello, Quarkus with mTLS from Config!");
        assert response.contains("Mutual TLS");
    }

    @Test
    // @Disabled("Enable this test when you want to test getServerTime with Quarkus client")
    public void testGetServerTimeWithQuarkusClient() throws Exception {
        System.out.println("Calling getServerTime on: " + serviceUrl);
        configureMutualTLS();
        // Call the service using the injected generated client
        String response = helloWorldService.getServerTime();

        System.out.println("GetServerTime Response: " + response);

        // Assertions
        assert response != null;
        assert response.contains("Current server time:");
    }

    @Test
    // @Disabled("Enable this test when you want to test echo with Quarkus client")
    public void testEchoWithQuarkusClient() throws Exception {
        String testMessage = "Testing mutual TLS with generated client from config";
        System.out.println("Calling echo on: " + serviceUrl);

        // Call the service using the injected generated client
        String response = helloWorldService.echo(testMessage);

        System.out.println("Echo Response: " + response);

        // Assertions
        assert response != null;
        assert response.contains("Echo: " + testMessage);
    }

    /**
     * Legacy test using manual SSL configuration (kept for reference)
     * The above Quarkus client approach is preferred
     */
    @Test
    @Disabled("Use testSoapServiceWithQuarkusClient instead - this is kept for reference")
    public void testSoapServiceCallManual() throws Exception {
        // Configure SSL with mutual TLS (client certificate authentication)
        configureMutualTLS();

        String soapEndpoint = "https://localhost:8444/soap/HelloWorldService";
        String soapRequest = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ns1:sayHello xmlns:ns1="http://example.org/">
                  <name>Quarkus</name>
                </ns1:sayHello>
              </soap:Body>
            </soap:Envelope>
            """;

        URL url = new URL(soapEndpoint);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        
        // Set request properties
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", "");
        connection.setDoOutput(true);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            os.write(soapRequest.getBytes());
        }

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        System.out.println("SOAP Response: " + response.toString());
        
        // Basic assertion
        assert response.toString().contains("Hello, Quarkus!");
    }

    @Test
    @Disabled("Use testGetServerTimeWithQuarkusClient instead - this is kept for reference")
    public void testGetServerTimeWithMutualTLSManual() throws Exception {
        // Configure SSL with mutual TLS
        configureMutualTLS();

        String soapEndpoint = "https://localhost:8444/soap/HelloWorldService";
        String soapRequest = """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ns1:getServerTime xmlns:ns1="http://example.org/" />
              </soap:Body>
            </soap:Envelope>
            """;

        URL url = new URL(soapEndpoint);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // Set request properties
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", "");
        connection.setDoOutput(true);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            os.write(soapRequest.getBytes());
        }

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        System.out.println("GetServerTime Response: " + response.toString());

        // Basic assertion - should contain current server time
        assert response.toString().contains("Current server time:");
    }

    /**
     * Configure SSL with mutual TLS (client certificate authentication)
     * WARNING: Only use this for testing! Never in production!
     */
    private void configureMutualTLS() throws Exception {
        // Load client certificate from keystore
        KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
        String clientKeystorePath = "client-keystore.p12";
        String keystorePassword = "changeit";

        // Try to load from project root first, then from classpath
        try (FileInputStream fis = new FileInputStream(clientKeystorePath)) {
            clientKeyStore.load(fis, keystorePassword.toCharArray());
        } catch (Exception e) {
            // If file not found in project root, try loading from classpath
            try (var is = getClass().getClassLoader().getResourceAsStream(clientKeystorePath)) {
                if (is == null) {
                    throw new RuntimeException("Client keystore not found. Please run generate-mtls-certificates.bat first.");
                }
                clientKeyStore.load(is, keystorePassword.toCharArray());
            }
        }

        // Set up KeyManager for client authentication
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientKeyStore, keystorePassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        // Trust all certificates (for testing with self-signed certificates)
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
        };

        // Initialize SSL context with both key managers and trust managers
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustAllCerts, new java.security.SecureRandom());

        // Set the SSL context for HTTPS connections
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}

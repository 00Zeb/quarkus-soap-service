package org.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

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
 * 2. Ensure client-keystore.p12 exists in the project root
 * 3. Start the Quarkus application with mutual TLS enabled
 *
 * Note: This test is disabled by default as it requires the service to be running
 */
//@QuarkusTest
public class SoapClientTest {

    @Test
   // @Disabled("Enable this test when you want to test the SOAP service manually")
    public void testSoapServiceCall() throws Exception {
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
    // @Disabled("Enable this test when you want to test the getServerTime method")
    public void testGetServerTimeWithMutualTLS() throws Exception {
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

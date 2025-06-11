package org.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * Test class demonstrating how to call the SOAP service
 * Note: This test is disabled by default as it requires the service to be running
 */
@QuarkusTest
public class SoapClientTest {

    @Test
    @Disabled("Enable this test when you want to test the SOAP service manually")
    public void testSoapServiceCall() throws Exception {
        // Disable SSL verification for self-signed certificates (for testing only!)
        disableSSLVerification();

        String soapEndpoint = "https://localhost:8443/soap/HelloWorldService";
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

    /**
     * Disable SSL verification for testing with self-signed certificates
     * WARNING: Only use this for testing! Never in production!
     */
    private void disableSSLVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}

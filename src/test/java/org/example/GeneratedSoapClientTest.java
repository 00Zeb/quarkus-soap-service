package org.example;

import org.example.client.HelloWorldService;
import org.example.client.HelloWorldService_Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocketFactory;
import jakarta.xml.ws.BindingProvider;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * Test class demonstrating how to use the generated SOAP client code with mutual TLS
 *
 * Prerequisites:
 * 1. Run generate-mtls-certificates.bat to create client certificates
 * 2. Copy client-keystore.p12 to src/test/resources/ (or run copy-client-cert-for-tests.bat)
 * 3. Start the Quarkus application with mutual TLS enabled
 * 4. Run ./mvnw clean generate-sources to generate client code from WSDL
 *
 * This test uses the generated client classes from target/generated-sources/cxf/
 */
public class GeneratedSoapClientTest {

    /**
     * Test using the generated SOAP client with manual mutual TLS configuration
     * This demonstrates how to use the generated client classes directly
     */
    @Test
    // @Disabled("Enable this test when you want to test with generated client code manually")
    public void testSoapServiceWithGeneratedClient() throws Exception {
        // Create the service using generated client code
        HelloWorldService_Service service = new HelloWorldService_Service();
        HelloWorldService port = service.getHelloWorldPort();

        // Configure the endpoint URL for the port
        BindingProvider bindingProvider = (BindingProvider) port;
        bindingProvider.getRequestContext().put(
            BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            "https://localhost:8444/soap/HelloWorldService"
        );

        // Configure SSL with mutual TLS for the CXF client
        configureCXFClientSSL(port);

        // Call the service methods
        String response1 = port.sayHello("Generated Client User");
        System.out.println("sayHello Response: " + response1);

        String response2 = port.getServerTime();
        System.out.println("getServerTime Response: " + response2);

        String response3 = port.echo("Testing with generated client");
        System.out.println("echo Response: " + response3);

        // Assertions
        assert response1 != null;
        assert response1.contains("Hello, Generated Client User!");
        assert response1.contains("Mutual TLS");

        assert response2 != null;
        assert response2.contains("Current server time:");

        assert response3 != null;
        assert response3.contains("Echo: Testing with generated client");
    }

    /**
     * Configure SSL with mutual TLS for CXF client
     * This method configures the CXF client to use mutual TLS with self-signed certificates
     */
    private void configureCXFClientSSL(HelloWorldService port) throws Exception {
        // Get the CXF client from the port
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

        // Create TLS client parameters
        TLSClientParameters tlsParams = new TLSClientParameters();

        // Disable hostname verification for self-signed certificates
        tlsParams.setDisableCNCheck(true);

        // Load client keystore for mutual TLS
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
        tlsParams.setKeyManagers(kmf.getKeyManagers());

        // Trust all certificates (for testing with self-signed certificates)
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
        };
        tlsParams.setTrustManagers(trustAllCerts);

        // Apply TLS parameters to the HTTP conduit
        httpConduit.setTlsClientParameters(tlsParams);
    }

    /**
     * Configure SSL with mutual TLS (client certificate authentication)
     * WARNING: Only use this for testing! Never in production!
     * @deprecated Use configureCXFClientSSL instead for CXF clients
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

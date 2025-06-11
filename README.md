# Quarkus SOAP Service with Mutual TLS

A simple Quarkus application that provides a SOAP web service over HTTPS using mutual TLS (mTLS) with self-signed certificates.

## Features

- 🚀 **Quarkus Framework**: Fast startup and low memory footprint
- 📡 **SOAP Web Service**: JAX-WS compliant SOAP service using Apache CXF
- 🔒 **Mutual TLS Support**: Secure communication with client certificate authentication
- 🔑 **Client Authentication**: Server validates client certificates for enhanced security
- ⚡ **Health Checks**: REST endpoints for monitoring
- 🛠️ **Development Ready**: Hot reload in development mode

## Prerequisites

- Java 17 or higher
- Maven 3.8.1 or higher

## Quick Start

1. **Clone and navigate to the project directory**
   ```bash
   cd quarkus-soap-service
   ```

2. **Generate mutual TLS certificates**
   ```bash
   # On Windows
   generate-mtls-certificates.bat

   # On Linux/Mac
   ./generate-mtls-certificates.sh
   ```

3. **Run in development mode**
   ```bash
   mvn clean compile quarkus:dev
   ```

4. **Access the application** (requires client certificate)
   - **SOAP Service**: https://localhost:8444/soap
   - **WSDL**: https://localhost:8444/soap/HelloWorldService?wsdl
   - **Health Check**: https://localhost:8444/health
   - **Service Info**: https://localhost:8444/health/info

## SOAP Service Methods

The `HelloWorldService` provides three methods:

### 1. sayHello(name)
Returns a personalized greeting message.

**Example Request:**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns1:sayHello xmlns:ns1="http://example.org/">
      <name>John</name>
    </ns1:sayHello>
  </soap:Body>
</soap:Envelope>
```

### 2. getServerTime()
Returns the current server timestamp.

### 3. echo(message)
Echoes back the input message.

## Mutual TLS Configuration

The application uses mutual TLS (mTLS) with self-signed certificates for both server and client authentication:

### Server Certificate (for server authentication)
- **Keystore**: `src/main/resources/keystore.p12`
- **Password**: `changeit`
- **Algorithm**: RSA 2048-bit
- **Validity**: 365 days
- **Subject**: CN=localhost, OU=Development, O=Quarkus, L=Local, ST=Local, C=US

### Client Certificate (for client authentication)
- **Keystore**: `client-keystore.p12`
- **Certificate**: `client-cert.pem`
- **Password**: `changeit`
- **Algorithm**: RSA 2048-bit
- **Validity**: 365 days
- **Subject**: CN=client, OU=Development, O=Quarkus, L=Local, ST=Local, C=US

### Truststore (for validating client certificates)
- **Truststore**: `src/main/resources/truststore.p12`
- **Password**: `changeit`
- **Contains**: Client certificate for validation

### Browser Access

When accessing the HTTPS endpoints with a browser, you'll need to:
1. Install the client certificate (`client-keystore.p12`) in your browser
2. Accept the self-signed server certificate warning
3. Select the client certificate when prompted

## Testing the SOAP Service

### Using the Test Scripts
The easiest way to test the service is using the provided scripts:

```bash
# On Windows
test-soap.bat

# On Linux/Mac
./test-soap.sh
```

These scripts automatically use the client certificate for mutual TLS authentication.

### Using curl manually
```bash
# Test sayHello method with client certificate
curl -k --cert client-cert.pem --key client-key.pem \
  -X POST https://localhost:8444/soap/HelloWorldService \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns1:sayHello xmlns:ns1="http://example.org/">
      <name>World</name>
    </ns1:sayHello>
  </soap:Body>
</soap:Envelope>'
```

**Note**: You need both `client-cert.pem` and `client-key.pem` files for curl testing. On Windows, you may need OpenSSL to extract the private key from the PKCS12 keystore.

### Using SoapUI
1. Create a new SOAP project
2. Use WSDL URL: `https://localhost:8444/soap/HelloWorldService?wsdl`
3. Configure SSL settings:
   - Import `client-keystore.p12` as the client certificate
   - Accept the self-signed server certificate
4. Test the available methods

## Building for Production

```bash
# Create executable JAR
mvn clean package

# Run the JAR
java -jar target/quarkus-app/quarkus-run.jar
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# HTTPS Configuration with Mutual TLS
quarkus.http.ssl-port=8444
quarkus.http.ssl.certificate.key-store-file=keystore.p12
quarkus.http.ssl.certificate.key-store-password=changeit
quarkus.http.ssl.certificate.key-store-file-type=PKCS12

# Mutual TLS Configuration - Client Certificate Authentication
quarkus.http.ssl.certificate.trust-store-file=truststore.p12
quarkus.http.ssl.certificate.trust-store-password=changeit
quarkus.http.ssl.certificate.trust-store-file-type=PKCS12
quarkus.http.ssl.client-auth=required

# Redirect HTTP to HTTPS
quarkus.http.insecure-requests=redirect

# SOAP Configuration
quarkus.cxf.path=/soap
```

## Development

### Hot Reload
In development mode (`mvn quarkus:dev`), the application supports hot reload. Changes to Java files will be automatically recompiled and reloaded.

### Logs
The application includes detailed logging for CXF and SOAP operations. Check the console output for debugging information.

## Troubleshooting

### Certificate Issues
If you encounter certificate issues:
1. Ensure all certificate files exist:
   - `src/main/resources/keystore.p12` (server certificate)
   - `src/main/resources/truststore.p12` (client certificate trust store)
   - `client-keystore.p12` (client certificate)
   - `client-cert.pem` (client certificate in PEM format)
2. Verify the password in `application.properties` (default: `changeit`)
3. Regenerate all certificates using the provided script:
   ```bash
   # On Windows
   generate-mtls-certificates.bat

   # On Linux/Mac
   ./generate-mtls-certificates.sh
   ```

### Client Certificate Issues
If curl tests fail with SSL errors:
1. Ensure `client-cert.pem` and `client-key.pem` files exist
2. On Windows, you may need OpenSSL to extract the private key:
   ```bash
   openssl pkcs12 -in client-keystore.p12 -nocerts -nodes -passin pass:changeit -out client-key.pem
   ```

### Port Conflicts
If port 8444 is already in use, modify `quarkus.http.ssl-port` in `application.properties`.

### Connection Refused
If you get "connection refused" errors:
1. Ensure the application is running (`mvn quarkus:dev`)
2. Check that certificates are generated and in the correct locations
3. Verify you're using the correct port (8444) and protocol (https)

## License

This project is for demonstration purposes.

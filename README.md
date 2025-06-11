# Quarkus SOAP Service with HTTPS

A simple Quarkus application that provides a SOAP web service over HTTPS using a self-signed certificate.

## Features

- 🚀 **Quarkus Framework**: Fast startup and low memory footprint
- 📡 **SOAP Web Service**: JAX-WS compliant SOAP service using Apache CXF
- 🔒 **HTTPS Support**: Secure communication with self-signed certificates
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

2. **Run in development mode**
   ```bash
   mvn clean compile quarkus:dev
   ```

3. **Access the application**
   - **SOAP Service**: https://localhost:8443/soap
   - **WSDL**: https://localhost:8443/soap/HelloWorldService?wsdl
   - **Health Check**: https://localhost:8443/health
   - **Service Info**: https://localhost:8443/health/info

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

## HTTPS Configuration

The application uses a self-signed certificate for HTTPS. The certificate details:
- **Keystore**: `src/main/resources/keystore.p12`
- **Password**: `changeit`
- **Algorithm**: RSA 2048-bit
- **Validity**: 365 days
- **Subject**: CN=localhost, OU=Development, O=Quarkus, L=Local, ST=Local, C=US

### Browser Security Warning

When accessing the HTTPS endpoints, your browser will show a security warning because the certificate is self-signed. This is expected for development. Click "Advanced" and "Proceed to localhost" to continue.

## Testing the SOAP Service

### Using curl
```bash
# Test sayHello method
curl -k -X POST https://localhost:8443/soap/HelloWorldService \
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

### Using SoapUI
1. Create a new SOAP project
2. Use WSDL URL: `https://localhost:8443/soap/HelloWorldService?wsdl`
3. Accept the self-signed certificate
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
# HTTPS Configuration
quarkus.http.ssl-port=8443
quarkus.http.ssl.certificate.key-store-file=keystore.p12
quarkus.http.ssl.certificate.key-store-password=changeit
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
1. Ensure the keystore file exists: `src/main/resources/keystore.p12`
2. Verify the password in `application.properties`
3. Regenerate the certificate if needed:
   ```bash
   keytool -genkeypair -storepass changeit -keypass changeit -keyalg RSA -keysize 2048 \
     -dname "CN=localhost,OU=Development,O=Quarkus,L=Local,ST=Local,C=US" \
     -alias quarkus-soap -keystore src/main/resources/keystore.p12 \
     -storetype PKCS12 -validity 365
   ```

### Port Conflicts
If port 8443 is already in use, modify `quarkus.http.ssl-port` in `application.properties`.

## License

This project is for demonstration purposes.

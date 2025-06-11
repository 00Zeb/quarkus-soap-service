#!/bin/bash

echo "🔐 Generating Mutual TLS Certificates for Quarkus SOAP Service"
echo "=============================================================="
echo ""

# Configuration
SERVER_KEYSTORE="src/main/resources/keystore.p12"
CLIENT_KEYSTORE="client-keystore.p12"
TRUSTSTORE="src/main/resources/truststore.p12"
CLIENT_CERT="client-cert.pem"
CLIENT_KEY="client-key.pem"
PASSWORD="changeit"

# Clean up existing certificates
echo "🧹 Cleaning up existing certificates..."
rm -f "$SERVER_KEYSTORE" "$CLIENT_KEYSTORE" "$TRUSTSTORE" "$CLIENT_CERT" "$CLIENT_KEY"
rm -f server-cert.pem client-cert.crt server-cert.crt

echo ""
echo "🔑 Step 1: Generating Server Certificate (for server authentication)..."
keytool -genkeypair \
    -alias server \
    -keyalg RSA \
    -keysize 2048 \
    -validity 365 \
    -keystore "$SERVER_KEYSTORE" \
    -storetype PKCS12 \
    -storepass "$PASSWORD" \
    -keypass "$PASSWORD" \
    -dname "CN=localhost,OU=Development,O=Quarkus,L=Local,ST=Local,C=US"

echo ""
echo "🔑 Step 2: Generating Client Certificate (for client authentication)..."
keytool -genkeypair \
    -alias client \
    -keyalg RSA \
    -keysize 2048 \
    -validity 365 \
    -keystore "$CLIENT_KEYSTORE" \
    -storetype PKCS12 \
    -storepass "$PASSWORD" \
    -keypass "$PASSWORD" \
    -dname "CN=client,OU=Development,O=Quarkus,L=Local,ST=Local,C=US"

echo ""
echo "📋 Step 3: Exporting Client Certificate for Server Truststore..."
keytool -exportcert \
    -alias client \
    -keystore "$CLIENT_KEYSTORE" \
    -storetype PKCS12 \
    -storepass "$PASSWORD" \
    -file client-cert.crt

echo ""
echo "🏪 Step 4: Creating Server Truststore with Client Certificate..."
keytool -importcert \
    -alias client \
    -file client-cert.crt \
    -keystore "$TRUSTSTORE" \
    -storetype PKCS12 \
    -storepass "$PASSWORD" \
    -noprompt

echo ""
echo "📄 Step 5: Exporting Client Certificate and Key for curl testing..."
# Export client certificate in PEM format
keytool -exportcert \
    -alias client \
    -keystore "$CLIENT_KEYSTORE" \
    -storetype PKCS12 \
    -storepass "$PASSWORD" \
    -rfc \
    -file "$CLIENT_CERT"

# Export client private key (requires conversion via openssl)
echo "Converting client keystore to PEM format for curl..."
if command -v openssl >/dev/null 2>&1; then
    openssl pkcs12 -in "$CLIENT_KEYSTORE" -nocerts -nodes -passin pass:"$PASSWORD" -out "$CLIENT_KEY"
    echo "✅ Client private key exported to $CLIENT_KEY"
else
    echo "⚠️  OpenSSL not found. You'll need to install OpenSSL to extract the private key for curl testing."
    echo "   Alternatively, use the client-keystore.p12 file directly with tools that support PKCS12 format."
fi

echo ""
echo "🧹 Cleaning up temporary files..."
rm -f client-cert.crt

echo ""
echo "✅ Mutual TLS Certificates Generated Successfully!"
echo ""
echo "📁 Generated Files:"
echo "   Server Keystore: $SERVER_KEYSTORE"
echo "   Server Truststore: $TRUSTSTORE"
echo "   Client Keystore: $CLIENT_KEYSTORE"
echo "   Client Certificate (PEM): $CLIENT_CERT"
echo "   Client Private Key (PEM): $CLIENT_KEY"
echo ""
echo "🔒 All keystores use password: $PASSWORD"
echo ""
echo "⚠️  Note: Keep client-keystore.p12, client-cert.pem, and client-key.pem"
echo "    safe as they contain the client credentials for mTLS authentication."
echo ""
echo "🚀 You can now start the Quarkus application with mutual TLS enabled!"

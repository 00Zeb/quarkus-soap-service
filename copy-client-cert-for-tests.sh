#!/bin/bash

echo "📋 Copying client certificate for test usage..."
echo ""

# Check if client keystore exists
if [ ! -f "client-keystore.p12" ]; then
    echo "❌ Client keystore not found: client-keystore.p12"
    echo "   Please run generate-mtls-certificates.sh first"
    exit 1
fi

# Create test resources directory if it doesn't exist
mkdir -p "src/test/resources"

# Copy client keystore to test resources
cp "client-keystore.p12" "src/test/resources/"

echo "✅ Client keystore copied to src/test/resources/"
echo ""
echo "🧪 You can now run the SoapClientTest with mutual TLS support"
echo ""

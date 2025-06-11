#!/bin/bash

echo "🧪 Testing Quarkus SOAP Service with HTTPS"
echo "=========================================="
echo ""

SOAP_URL="https://localhost:8443/soap/HelloWorldService"

echo "📡 Testing SOAP Service at: $SOAP_URL"
echo ""

# Test 1: sayHello method
echo "🔹 Test 1: sayHello method"
curl -k -X POST $SOAP_URL \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns1:sayHello xmlns:ns1="http://example.org/">
      <name>Quarkus User</name>
    </ns1:sayHello>
  </soap:Body>
</soap:Envelope>'

echo ""
echo ""

# Test 2: getServerTime method
echo "🔹 Test 2: getServerTime method"
curl -k -X POST $SOAP_URL \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns1:getServerTime xmlns:ns1="http://example.org/" />
  </soap:Body>
</soap:Envelope>'

echo ""
echo ""

# Test 3: echo method
echo "🔹 Test 3: echo method"
curl -k -X POST $SOAP_URL \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns1:echo xmlns:ns1="http://example.org/">
      <message>Hello from curl test!</message>
    </ns1:echo>
  </soap:Body>
</soap:Envelope>'

echo ""
echo ""

# Test 4: Get WSDL
echo "🔹 Test 4: Retrieving WSDL"
curl -k -X GET "https://localhost:8443/soap/HelloWorldService?wsdl"

echo ""
echo ""

# Test 5: Health check
echo "🔹 Test 5: Health check"
curl -k -X GET "https://localhost:8443/health"

echo ""
echo ""
echo "✅ All tests completed!"
echo ""
echo "Note: The -k flag is used to ignore SSL certificate verification"
echo "      since we're using a self-signed certificate for development."

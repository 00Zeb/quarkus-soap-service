@echo off
echo 🧪 Testing Quarkus SOAP Service with Mutual TLS
echo ===============================================
echo.

set SOAP_URL=https://localhost:8444/soap/HelloWorldService
set CLIENT_CERT=client-cert.pem
set CLIENT_KEY=client-key.pem

echo 📡 Testing SOAP Service at: %SOAP_URL%
echo 🔐 Using Mutual TLS with client certificate: %CLIENT_CERT%
echo.

REM Check if client certificate files exist
if not exist "%CLIENT_CERT%" (
    echo ❌ Client certificate file not found: %CLIENT_CERT%
    echo    Please run generate-mtls-certificates.bat first
    pause
    exit /b 1
)

echo.

echo 🔹 Test 1: sayHello method
curl -k --cert "%CLIENT_CERT%" -X POST %SOAP_URL% ^
  -H "Content-Type: text/xml; charset=utf-8" ^
  -H "SOAPAction: \"\"" ^
  -d "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns1:sayHello xmlns:ns1=\"http://example.org/\"><name>Quarkus User</name></ns1:sayHello></soap:Body></soap:Envelope>"

echo.
echo.

echo 🔹 Test 2: getServerTime method
curl -k --cert "%CLIENT_CERT%" -X POST %SOAP_URL% ^
  -H "Content-Type: text/xml; charset=utf-8" ^
  -H "SOAPAction: \"\"" ^
  -d "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns1:getServerTime xmlns:ns1=\"http://example.org/\" /></soap:Body></soap:Envelope>"

echo.
echo.

echo 🔹 Test 3: echo method
curl -k --cert "%CLIENT_CERT%" -X POST %SOAP_URL% ^
  -H "Content-Type: text/xml; charset=utf-8" ^
  -H "SOAPAction: \"\"" ^
  -d "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns1:echo xmlns:ns1=\"http://example.org/\"><message>Hello from curl test!</message></ns1:echo></soap:Body></soap:Envelope>"

echo.
echo.

echo 🔹 Test 4: Retrieving WSDL
curl -k --cert "%CLIENT_CERT%" -X GET "https://localhost:8444/soap/HelloWorldService?wsdl"

echo.
echo.

echo 🔹 Test 5: Health check
curl -k --cert "%CLIENT_CERT%" -X GET "https://localhost:8444/health"

echo.
echo.
echo ✅ All tests completed!
echo.
echo Note: The -k flag is used to ignore SSL certificate verification
echo       and --cert flag provides client certificate for mutual TLS.
echo.
pause

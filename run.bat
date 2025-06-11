@echo off
echo Starting Quarkus SOAP Service with Mutual TLS...
echo.
echo Please make sure you have Maven installed and available in your PATH
echo.
echo To run this application:
echo 1. Install Maven if not already installed
echo 2. Generate certificates: generate-mtls-certificates.bat
echo 3. Run: mvn clean compile quarkus:dev
echo.
echo The application will be available at:
echo - HTTPS with mTLS: https://localhost:8444
echo - SOAP Service: https://localhost:8444/soap
echo - WSDL: https://localhost:8444/soap/HelloWorldService?wsdl
echo - Health Check: https://localhost:8444/health
echo.
echo Note: Client certificate authentication is required for all connections
echo.
pause

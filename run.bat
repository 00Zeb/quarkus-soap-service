@echo off
echo Starting Quarkus SOAP Service with HTTPS...
echo.
echo Please make sure you have Maven installed and available in your PATH
echo.
echo To run this application:
echo 1. Install Maven if not already installed
echo 2. Run: mvn clean compile quarkus:dev
echo.
echo The application will be available at:
echo - HTTPS: https://localhost:8443
echo - SOAP Service: https://localhost:8443/soap
echo - WSDL: https://localhost:8443/soap/HelloWorldService?wsdl
echo - Health Check: https://localhost:8443/health
echo.
pause

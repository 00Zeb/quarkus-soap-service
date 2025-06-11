@echo off
echo 🔐 Generating Mutual TLS Certificates for Quarkus SOAP Service
echo ==============================================================
echo.

REM Configuration
set SERVER_KEYSTORE=src\main\resources\keystore.p12
set CLIENT_KEYSTORE=client-keystore.p12
set TRUSTSTORE=src\main\resources\truststore.p12
set CLIENT_CERT=client-cert.pem
set CLIENT_KEY=client-key.pem
set PASSWORD=changeit

REM Clean up existing certificates
echo 🧹 Cleaning up existing certificates...
if exist "%SERVER_KEYSTORE%" del "%SERVER_KEYSTORE%"
if exist "%CLIENT_KEYSTORE%" del "%CLIENT_KEYSTORE%"
if exist "%TRUSTSTORE%" del "%TRUSTSTORE%"
if exist "%CLIENT_CERT%" del "%CLIENT_CERT%"
if exist "%CLIENT_KEY%" del "%CLIENT_KEY%"
if exist "client-cert.crt" del "client-cert.crt"

echo.
echo 🔑 Step 1: Generating Server Certificate...
keytool -genkeypair ^
    -alias server ^
    -keyalg RSA ^
    -keysize 2048 ^
    -validity 365 ^
    -keystore "%SERVER_KEYSTORE%" ^
    -storetype PKCS12 ^
    -storepass "%PASSWORD%" ^
    -keypass "%PASSWORD%" ^
    -dname "CN=localhost,OU=Development,O=Quarkus,L=Local,ST=Local,C=US"

echo.
echo 🔑 Step 2: Generating Client Certificate...
keytool -genkeypair ^
    -alias client ^
    -keyalg RSA ^
    -keysize 2048 ^
    -validity 365 ^
    -keystore "%CLIENT_KEYSTORE%" ^
    -storetype PKCS12 ^
    -storepass "%PASSWORD%" ^
    -keypass "%PASSWORD%" ^
    -dname "CN=client,OU=Development,O=Quarkus,L=Local,ST=Local,C=US"

echo.
echo 📋 Step 3: Exporting Client Certificate...
keytool -exportcert ^
    -alias client ^
    -keystore "%CLIENT_KEYSTORE%" ^
    -storetype PKCS12 ^
    -storepass "%PASSWORD%" ^
    -file client-cert.crt

echo.
echo 🏪 Step 4: Creating Server Truststore...
keytool -importcert ^
    -alias client ^
    -file client-cert.crt ^
    -keystore "%TRUSTSTORE%" ^
    -storetype PKCS12 ^
    -storepass "%PASSWORD%" ^
    -noprompt

echo.
echo 📄 Step 5: Exporting Client Certificate in PEM format...
keytool -exportcert ^
    -alias client ^
    -keystore "%CLIENT_KEYSTORE%" ^
    -storetype PKCS12 ^
    -storepass "%PASSWORD%" ^
    -rfc ^
    -file "%CLIENT_CERT%"

echo.
echo 🧹 Cleaning up temporary files...
if exist "client-cert.crt" del "client-cert.crt"

echo.
echo ✅ Certificates Generated Successfully!
echo.
echo 📁 Files created:
echo    Server Keystore: %SERVER_KEYSTORE%
echo    Server Truststore: %TRUSTSTORE%
echo    Client Keystore: %CLIENT_KEYSTORE%
echo    Client Certificate: %CLIENT_CERT%
echo.
pause

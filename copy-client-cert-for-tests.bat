@echo off
echo 📋 Copying client certificate for test usage...
echo.

REM Check if client keystore exists
if not exist "client-keystore.p12" (
    echo ❌ Client keystore not found: client-keystore.p12
    echo    Please run generate-mtls-certificates.bat first
    pause
    exit /b 1
)

REM Create test resources directory if it doesn't exist
if not exist "src\test\resources" (
    mkdir "src\test\resources"
)

REM Copy client keystore to test resources
copy "client-keystore.p12" "src\test\resources\"

echo ✅ Client keystore copied to src\test\resources\
echo.
echo 🧪 You can now run the SoapClientTest with mutual TLS support
echo.
pause

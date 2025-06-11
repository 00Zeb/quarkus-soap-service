# PowerShell script to run Quarkus SOAP Service
Write-Host "🚀 Starting Quarkus SOAP Service with HTTPS..." -ForegroundColor Green
Write-Host ""

# Check if Maven is installed
try {
    $mvnVersion = mvn --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Maven found" -ForegroundColor Green
        Write-Host ""
        Write-Host "🔧 Building and starting the application..." -ForegroundColor Yellow
        Write-Host ""
        
        # Run the application
        mvn clean compile quarkus:dev
    } else {
        throw "Maven not found"
    }
} catch {
    Write-Host "❌ Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Maven first:" -ForegroundColor Yellow
    Write-Host "1. Download Maven from: https://maven.apache.org/download.cgi"
    Write-Host "2. Extract and add to your PATH"
    Write-Host "3. Run this script again"
    Write-Host ""
    Write-Host "Alternative: Use your IDE's Maven integration"
    Write-Host ""
    Write-Host "Once running, the application will be available at:"
    Write-Host "- HTTPS: https://localhost:8443" -ForegroundColor Cyan
    Write-Host "- SOAP Service: https://localhost:8443/soap" -ForegroundColor Cyan
    Write-Host "- WSDL: https://localhost:8443/soap/HelloWorldService?wsdl" -ForegroundColor Cyan
    Write-Host "- Health Check: https://localhost:8443/health" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

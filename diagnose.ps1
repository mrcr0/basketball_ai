# Basketball Training Platform - System Diagnostic
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Basketball Platform - Diagnostic Report" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Check Frontend Service
Write-Host "[1/6] Checking Frontend Service..." -ForegroundColor Yellow
$frontendPorts = @(5173, 5174, 5175)
foreach ($port in $frontendPorts) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$port" -Method GET -TimeoutSec 3 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "  [OK] Frontend running on port $port" -ForegroundColor Green
            $global:frontendPort = $port
            break
        }
    } catch {
        if ($port -eq 5175) {
            Write-Host "  [FAIL] Frontend not running on any port" -ForegroundColor Red
        }
    }
}

# 2. Check Backend Service
Write-Host ""
Write-Host "[2/6] Checking Backend Service..." -ForegroundColor Yellow
try {
    $backendResponse = Invoke-WebRequest -Uri "http://localhost:8080" -Method GET -TimeoutSec 5 -ErrorAction SilentlyContinue
    Write-Host "  [OK] Backend running on port 8080" -ForegroundColor Green
} catch {
    Write-Host "  [FAIL] Backend not running" -ForegroundColor Red
}

# 3. Test Login API
Write-Host ""
Write-Host "[3/6] Testing Login API..." -ForegroundColor Yellow
$loginBody = @{username="admin";password="123456"} | ConvertTo-Json
try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/login" -Method POST -Body $loginBody -ContentType "application/json" -TimeoutSec 10
    if ($loginResponse.code -eq 200) {
        Write-Host "  [OK] Login API works" -ForegroundColor Green
        Write-Host "    UserID: $($loginResponse.data.user.id)" -ForegroundColor Cyan
        Write-Host "    Username: $($loginResponse.data.user.username)" -ForegroundColor Cyan
        $global:token = $loginResponse.data.token
    } else {
        Write-Host "  [FAIL] Login failed (code: $($loginResponse.code))" -ForegroundColor Red
    }
} catch {
    Write-Host "  [FAIL] Login API request failed: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Test Register API
Write-Host ""
Write-Host "[4/6] Testing Register API..." -ForegroundColor Yellow
$timestamp = [int](Get-Date -UFormat %s)
$registerBody = @{username="testuser$timestamp";password="123456";nickname="Test$timestamp"} | ConvertTo-Json
try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -Body $registerBody -ContentType "application/json" -TimeoutSec 10
    if ($registerResponse.code -eq 200) {
        Write-Host "  [OK] Register API works" -ForegroundColor Green
        Write-Host "    NewUserID: $($registerResponse.data.id)" -ForegroundColor Cyan
    } else {
        Write-Host "  [FAIL] Register failed (code: $($registerResponse.code))" -ForegroundColor Red
    }
} catch {
    Write-Host "  [FAIL] Register API request failed: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. Check Proxy Configuration
Write-Host ""
Write-Host "[5/6] Checking Proxy Configuration..." -ForegroundColor Yellow
if (Test-Path "frontend\vite.config.js") {
    $content = Get-Content "frontend\vite.config.js" -Raw
    if ($content -match "target.*localhost:8080") {
        Write-Host "  [OK] Proxy configured correctly" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] Proxy target may be incorrect" -ForegroundColor Yellow
    }
} else {
    Write-Host "  [FAIL] vite.config.js not found" -ForegroundColor Red
}

# 6. Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
if ($global:frontendPort -and $global:token) {
    Write-Host "Access URL: http://localhost:$global:frontendPort" -ForegroundColor Green
    Write-Host "Login Credentials: admin / 123456" -ForegroundColor Green
    Write-Host ""
    Write-Host "[SUCCESS] All services are running correctly!" -ForegroundColor Green
} else {
    Write-Host "[WARNING] Some services may not be working properly" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "If frontend still cannot login:" -ForegroundColor Magenta
Write-Host "1. Open Browser DevTools (F12)" -ForegroundColor Gray
Write-Host "2. Go to Network tab" -ForegroundColor Gray
Write-Host "3. Try to login/register" -ForegroundColor Gray
Write-Host "4. Check if requests go to /api/users/*" -ForegroundColor Gray
Write-Host "5. Check for CORS or 404 errors" -ForegroundColor Gray

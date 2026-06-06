# 篮球训练平台 - 系统诊断脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "篮球训练平台 - 系统诊断报告" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 检查前端服务状态
Write-Host "[1/6] 检查前端服务..." -ForegroundColor Yellow
try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost:5173" -Method GET -TimeoutSec 5 -ErrorAction SilentlyContinue
    if ($frontendResponse.StatusCode -eq 200) {
        Write-Host "  ✓ 前端服务运行正常 (localhost:5173)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 前端服务异常 (状态码: $($frontendResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "  ✗ 前端服务未运行或无法连接" -ForegroundColor Red
}

# 检查5174端口
try {
    $frontendResponse2 = Invoke-WebRequest -Uri "http://localhost:5174" -Method GET -TimeoutSec 3 -ErrorAction SilentlyContinue
    if ($frontendResponse2.StatusCode -eq 200) {
        Write-Host "  ✓ 前端服务运行在备选端口 (localhost:5174)" -ForegroundColor Green
    }
} catch {}

# 检查5175端口
try {
    $frontendResponse3 = Invoke-WebRequest -Uri "http://localhost:5175" -Method GET -TimeoutSec 3 -ErrorAction SilentlyContinue
    if ($frontendResponse3.StatusCode -eq 200) {
        Write-Host "  ✓ 前端服务运行在备选端口 (localhost:5175)" -ForegroundColor Green
    }
} catch {}

# 2. 检查后端服务状态
Write-Host ""
Write-Host "[2/6] 检查后端服务..." -ForegroundColor Yellow
try {
    $backendResponse = Invoke-WebRequest -Uri "http://localhost:8080" -Method GET -TimeoutSec 5 -ErrorAction SilentlyContinue
    Write-Host "  ✓ 后端服务运行正常 (localhost:8080)" -ForegroundColor Green
} catch {
    Write-Host "  ✗ 后端服务未运行或无法连接" -ForegroundColor Red
}

# 3. 测试登录API
Write-Host ""
Write-Host "[3/6] 测试登录API..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/login" -Method POST -Body $loginBody -ContentType "application/json" -TimeoutSec 10 -ErrorAction SilentlyContinue
    if ($loginResponse.code -eq 200) {
        Write-Host "  ✓ 登录API正常" -ForegroundColor Green
        Write-Host "    - 用户ID: $($loginResponse.data.user.id)" -ForegroundColor Cyan
        Write-Host "    - 用户名: $($loginResponse.data.user.username)" -ForegroundColor Cyan
        Write-Host "    - 角色: $($loginResponse.data.user.role)" -ForegroundColor Cyan
        $global:testToken = $loginResponse.data.token
    } else {
        Write-Host "  ✗ 登录失败 (错误码: $($loginResponse.code))" -ForegroundColor Red
        Write-Host "    - 错误信息: $($loginResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "  ✗ 登录API请求失败" -ForegroundColor Red
    Write-Host "    - 错误: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 测试注册API
Write-Host ""
Write-Host "[4/6] 测试注册API..." -ForegroundColor Yellow
$timestamp = Get-Date -Format "HHmmss"
$registerBody = @{
    username = "testuser$timestamp"
    password = "123456"
    nickname = "测试用户$timestamp"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" -Method POST -Body $registerBody -ContentType "application/json" -TimeoutSec 10 -ErrorAction SilentlyContinue
    if ($registerResponse.code -eq 200) {
        Write-Host "  ✓ 注册API正常" -ForegroundColor Green
        Write-Host "    - 新用户ID: $($registerResponse.data.id)" -ForegroundColor Cyan
        Write-Host "    - 新用户名: $($registerResponse.data.username)" -ForegroundColor Cyan
    } else {
        Write-Host "  ✗ 注册失败 (错误码: $($registerResponse.code))" -ForegroundColor Red
        Write-Host "    - 错误信息: $($registerResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "  ✗ 注册API请求失败" -ForegroundColor Red
    Write-Host "    - 错误: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 检查代理配置
Write-Host ""
Write-Host "[5/6] 检查前端代理配置..." -ForegroundColor Yellow
if (Test-Path "frontend\vite.config.js") {
    $proxyConfig = Get-Content "frontend\vite.config.js" -Raw
    if ($proxyConfig -match "target.*localhost:8080") {
        Write-Host "  ✓ 代理配置正确 (target: http://localhost:8080)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 代理配置可能不正确" -ForegroundColor Red
    }
} else {
    Write-Host "  ✗ vite.config.js 文件不存在" -ForegroundColor Red
}

# 6. 检查数据库连接
Write-Host ""
Write-Host "[6/6] 检查数据库..." -ForegroundColor Yellow
try {
    # 尝试访问H2控制台
    $h2Response = Invoke-WebRequest -Uri "http://localhost:8080/h2-console" -Method GET -TimeoutSec 5 -ErrorAction SilentlyContinue
    Write-Host "  ✓ H2数据库控制台可访问" -ForegroundColor Green
} catch {
    Write-Host "  ⚠ H2数据库控制台无法访问 (可能正常)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "诊断完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 提供解决方案提示
Write-Host ""
Write-Host "【解决方案提示】" -ForegroundColor Magenta
Write-Host "如果前端仍然无法登录/注册:" -ForegroundColor White
Write-Host "1. 打开浏览器开发者工具 (F12)" -ForegroundColor Gray
Write-Host "2. 切换到 Network (网络) 标签页" -ForegroundColor Gray
Write-Host "3. 尝试登录/注册操作" -ForegroundColor Gray
Write-Host "4. 查看 /api/users/login 或 /api/users/register 请求" -ForegroundColor Gray
Write-Host "5. 检查请求和响应的详细信息" -ForegroundColor Gray
Write-Host ""
Write-Host "如果看到 CORS 错误或 404 错误，说明前端代理未正确工作" -ForegroundColor Yellow
Write-Host "请确保前端运行在 http://localhost:5173" -ForegroundColor Yellow

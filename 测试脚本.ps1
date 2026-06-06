# 篮球训练系统 - 登录测试

Write-Host "========================================"
Write-Host "  篮球训练系统 - 登录测试"
Write-Host "========================================"
Write-Host ""

# 测试管理员登录
Write-Host "[测试] 管理员登录 (admin/123456)..."
$body = @{
    username = "admin"
    password = "123456"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/users/login" -Method POST -ContentType "application/json" -Body $body -TimeoutSec 10
    if ($response.code -eq 200) {
        Write-Host "SUCCESS: 管理员登录成功" -ForegroundColor Green
        Write-Host "  Token: $($response.data.token.Substring(0, 50))..."
    } else {
        Write-Host "FAILED: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "ERROR: $_" -ForegroundColor Red
}

Write-Host ""

# 测试普通用户登录
Write-Host "[测试] 普通用户登录 (player1/123456)..."
$body = @{
    username = "player1"
    password = "123456"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/users/login" -Method POST -ContentType "application/json" -Body $body -TimeoutSec 10
    if ($response.code -eq 200) {
        Write-Host "SUCCESS: 用户登录成功" -ForegroundColor Green
        Write-Host "  角色: $($response.data.user.role)"
    } else {
        Write-Host "FAILED: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "ERROR: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================"
Write-Host "测试完成"
Write-Host "========================================"
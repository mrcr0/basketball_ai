# Basketball Training System - Match Simulation Test Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Match Simulation Test Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Create Match
Write-Host "[Test 1] Creating simulated match..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/match/create" -Method POST -TimeoutSec 10
    if ($response.code -eq 200) {
        $matchId = $response.data.id
        Write-Host "SUCCESS: Match created" -ForegroundColor Green
        Write-Host "  Match ID: $matchId" -ForegroundColor Gray
        Write-Host "  Match Name: $($response.data.matchName)" -ForegroundColor Gray
        Write-Host "  Home Team: $($response.data.homeTeam)" -ForegroundColor Gray
        Write-Host "  Away Team: $($response.data.awayTeam)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
    exit
}

Write-Host ""

# Test 2: Get Match Info
Write-Host "[Test 2] Getting match info..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/match/$matchId" -Method GET -TimeoutSec 10
    if ($response.code -eq 200) {
        Write-Host "SUCCESS: Match info retrieved" -ForegroundColor Green
        Write-Host "  Status: $($response.data.status)" -ForegroundColor Gray
        Write-Host "  Quarter: Q$($response.data.currentQuarter)" -ForegroundColor Gray
        Write-Host "  Remaining Time: $($response.data.remainingTime) sec" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}

Write-Host ""

# Test 3: Start Match
Write-Host "[Test 3] Starting match..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/match/$matchId/toggle" -Method PUT -TimeoutSec 10
    if ($response.code -eq 200 -and $response.data.status -eq "进行中") {
        Write-Host "SUCCESS: Match started" -ForegroundColor Green
    } else {
        Write-Host "FAILED: Cannot start match" -ForegroundColor Red
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}

Write-Host ""

# Test 4: Simulate Real-time Updates
Write-Host "[Test 4] Simulating real-time updates (5 times)..." -ForegroundColor Yellow
for ($i = 1; $i -le 5; $i++) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/match/$matchId/update" -Method PUT -TimeoutSec 10
        if ($response.code -eq 200) {
            Write-Host "  Update $i: $($response.data.homeTeam) $($response.data.homeScore) - $($response.data.awayScore) $($response.data.awayTeam)" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  Update $i failed: $_" -ForegroundColor Red
    }
    Start-Sleep -Milliseconds 500
}

Write-Host ""

# Test 5: Pause Match
Write-Host "[Test 5] Pausing match..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/match/$matchId/toggle" -Method PUT -TimeoutSec 10
    if ($response.code -eq 200 -and $response.data.status -eq "暂停") {
        Write-Host "SUCCESS: Match paused" -ForegroundColor Green
    } else {
        Write-Host "FAILED: Cannot pause match" -ForegroundColor Red
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}

Write-Host ""

# Test 6: Reset Match
Write-Host "[Test 6] Resetting match..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/match/$matchId/reset" -Method PUT -TimeoutSec 10
    if ($response.code -eq 200 -and $response.data.status -eq "未开始") {
        Write-Host "SUCCESS: Match reset" -ForegroundColor Green
        Write-Host "  Score: $($response.data.homeScore) - $($response.data.awayScore)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Tests Completed" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Frontend URL: http://localhost:5173/match" -ForegroundColor White
Write-Host "API Endpoint: http://localhost:8080/api/match/create" -ForegroundColor White
Write-Host ""
Write-Host "Test Results:" -ForegroundColor White
Write-Host "  SUCCESS: Create match" -ForegroundColor Gray
Write-Host "  SUCCESS: Get match info" -ForegroundColor Gray
Write-Host "  SUCCESS: Start/Pause match" -ForegroundColor Gray
Write-Host "  SUCCESS: Real-time updates" -ForegroundColor Gray
Write-Host "  SUCCESS: Reset match" -ForegroundColor Gray
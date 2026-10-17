# PowerShell Fast Testcase Loader for Library Management System
param (
    [string]$Mode = "standard"
)

$baseDir = $PSScriptRoot
$targetDir = Split-Path $baseDir -Parent

switch ($Mode.ToLower()) {
    "standard" {
        Write-Host ">> Loading [STANDARD] test suite..." -ForegroundColor Cyan
        Copy-Item "$baseDir\Books_Standard.txt" "$targetDir\Books.txt" -Force
        Copy-Item "$baseDir\Members_Standard.txt" "$targetDir\Members.txt" -Force
        Copy-Item "$baseDir\BorrowRecords_Standard.txt" "$targetDir\BorrowRecords.txt" -Force
        Write-Host ">> Successfully loaded 15 Books, 10 Members, 6 BorrowRecords!" -ForegroundColor Green
    }
    "outofstock" {
        Write-Host ">> Loading [OUT-OF-STOCK / RESERVATION] test suite..." -ForegroundColor Cyan
        Copy-Item "$baseDir\Books_EdgeCases_OutOfStock.txt" "$targetDir\Books.txt" -Force
        Copy-Item "$baseDir\Members_Standard.txt" "$targetDir\Members.txt" -Force
        Write-Host ">> Successfully loaded Out-of-Stock books for reservation queue testing!" -ForegroundColor Green
    }
    "duplicate" {
        Write-Host ">> Loading [DUPLICATE MEMBER ID ERROR] test suite..." -ForegroundColor Yellow
        Copy-Item "$baseDir\Members_Error_DuplicateID.txt" "$targetDir\Members.txt" -Force
        Write-Host ">> Loaded duplicate ID file. Next program run should trigger duplicate ID warning!" -ForegroundColor Yellow
    }
    default {
        Write-Host ">> Usage: .\load_testcase.ps1 [-Mode standard | outofstock | duplicate]" -ForegroundColor Red
    }
}

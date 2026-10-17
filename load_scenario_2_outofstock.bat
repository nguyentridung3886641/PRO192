@echo off
setlocal
cd /d "%~dp0"

echo ==============================================================================
echo [*] LOADING SCENARIO 2: OUT-OF-STOCK & RESERVATION QUEUE TEST SUITE
echo ==============================================================================
echo - Books with quantity = 0 (B101, B103, B105 out-of-stock items)
echo - Standard Members (M001, M002, M003...)
echo - Purpose: Test borrowing out-of-stock books and joining FIFO Reservation Queue!
echo.

copy /y "testcases\Books_EdgeCases_OutOfStock.txt" "Books.txt" >nul
copy /y "testcases\Members_Standard.txt" "Members.txt" >nul

echo [SUCCESS] Loaded out-of-stock Books.txt for Reservation Queue testing!
echo.
pause

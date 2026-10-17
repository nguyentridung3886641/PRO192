@echo off
setlocal
cd /d "%~dp0"

echo ==============================================================================
echo                FAST TESTCASE LOADER - LIBRARY MANAGEMENT SYSTEM
echo ==============================================================================
echo  1. Load [STANDARD] Suite (15 Books, 10 Members all tiers, 6 Loans/Overdue)
echo  2. Load [OUT-OF-STOCK] Suite (Books with quantity=0 for reservation queue)
echo  3. Load [DUPLICATE ID ERROR] Suite (Negative test for duplicate Member ID)
echo  0. Cancel
echo ==============================================================================
set "choice="
set /p "choice=Enter your choice [0-3]: "

if "%choice%"=="1" goto opt1
if "%choice%"=="2" goto opt2
if "%choice%"=="3" goto opt3
goto opt0

:opt1
echo.
echo [*] Copying STANDARD testsuite files...
copy /y "testcases\Books_Standard.txt" "Books.txt" >nul
copy /y "testcases\Members_Standard.txt" "Members.txt" >nul
copy /y "testcases\BorrowRecords_Standard.txt" "BorrowRecords.txt" >nul
echo [SUCCESS] Loaded 15 Books, 10 Members, 6 BorrowRecords!
goto end

:opt2
echo.
echo [*] Copying OUT-OF-STOCK testsuite files...
copy /y "testcases\Books_EdgeCases_OutOfStock.txt" "Books.txt" >nul
copy /y "testcases\Members_Standard.txt" "Members.txt" >nul
echo [SUCCESS] Loaded Out-of-Stock books for Reservation Queue testing!
goto end

:opt3
echo.
echo [*] Copying DUPLICATE ID ERROR testsuite files...
copy /y "testcases\Members_Error_DuplicateID.txt" "Members.txt" >nul
echo [SUCCESS] Loaded Duplicate ID file. Running the app will trigger validation error!
goto end

:opt0
echo.
echo Cancelled.

:end
echo.
pause

@echo off
setlocal
cd /d "%~dp0"

echo ==============================================================================
echo [*] LOADING SCENARIO 1: STANDARD TEST SUITE
echo ==============================================================================
echo - 15 Books (TextBooks discounted by 10%%, ReferenceBooks with 10%% tax)
echo - 10 Members (All tiers: VIP, GOLD, SILVER, and LOCKED cards)
echo - 6 BorrowRecords (Active loans, Overdue loans, and Damaged book compensation)
echo.

copy /y "testcases\Books_Standard.txt" "Books.txt" >nul
copy /y "testcases\Members_Standard.txt" "Members.txt" >nul
copy /y "testcases\BorrowRecords_Standard.txt" "BorrowRecords.txt" >nul

echo [SUCCESS] Testcase data loaded successfully into Books.txt, Members.txt, BorrowRecords.txt!
echo.
echo [*] Compiling Java source files...
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin BusinessObject\UI\*.java Core\Entities\*.java DataObjects\DAO\*.java Utilities\*.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo [*] Starting Library Management System...
echo ==============================================================================
echo.
java -cp bin BusinessObject.UI.Program

echo.
echo ==============================================================================
echo [*] Program finished.
pause

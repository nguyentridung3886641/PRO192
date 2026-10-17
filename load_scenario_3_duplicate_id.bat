@echo off
setlocal
cd /d "%~dp0"

echo ==============================================================================
echo [*] LOADING SCENARIO 3: NEGATIVE TEST (DUPLICATE MEMBER ID ERROR)
echo ==============================================================================
echo - Members.txt with duplicated IDs (M001 and m001 case-insensitive)
echo - Purpose: Test MemberDAO duplicate ID validation (HashSet anyMatch) and IOException
echo.

copy /y "testcases\Members_Error_DuplicateID.txt" "Members.txt" >nul

echo [SUCCESS] Testcase data loaded successfully into Members.txt!
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

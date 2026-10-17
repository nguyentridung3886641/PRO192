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

echo [SUCCESS] Loaded duplicate ID Members.txt!
echo [*] When starting the application, MemberDAO will catch the duplicate ID.
echo.
pause

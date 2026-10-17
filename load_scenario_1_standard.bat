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

echo [SUCCESS] Loaded Books.txt, Members.txt, BorrowRecords.txt!
echo.
pause

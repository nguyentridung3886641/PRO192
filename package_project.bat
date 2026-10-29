@echo off
chcp 65001 > nul
echo ====================================================
echo 1. Compiling Library Management System...
echo ====================================================
cd /d "C:\Users\Admin\OneDrive\FA26\PRO192\Project\Library Management"
call compile.bat
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo ====================================================
echo 2. Packaging Library_Management_Project.rar...
echo ====================================================
set RAR="C:\Program Files\WinRAR\rar.exe"
if not exist %RAR% (
    echo [ERROR] WinRAR not found at %RAR%
    pause
    exit /b 1
)

cd /d "C:\Users\Admin\OneDrive\FA26\PRO192\Project"
%RAR% a -r -y -m5 "-x*\.git\*" "-x*\bin\*" "-x*\.vscode\*" "C:\Users\Admin\OneDrive\Library_Management_Project.rar" "Library Management"
copy /y "C:\Users\Admin\OneDrive\Library_Management_Project.rar" "C:\Users\Admin\OneDrive\FA26\PRO192\Library_Management_Project.rar" > nul

echo ====================================================
echo 3. Packaging PRO192_Group4_LibraryManagement.rar (Code + Report + Slides)...
echo ====================================================
set STAGING="C:\Users\Admin\OneDrive\PRO192_Group4_Submission"
if exist %STAGING% rd /s /q %STAGING%
mkdir %STAGING%\01_Source_Code
mkdir %STAGING%\02_Report
mkdir %STAGING%\03_Presentation_Slides

:: Copy clean source code
xcopy /e /i /y "C:\Users\Admin\OneDrive\FA26\PRO192\Project\Library Management\*" %STAGING%\01_Source_Code\ > nul
if exist %STAGING%\01_Source_Code\bin rd /s /q %STAGING%\01_Source_Code\bin
if exist %STAGING%\01_Source_Code\.git rd /s /q %STAGING%\01_Source_Code\.git
if exist %STAGING%\01_Source_Code\.vscode rd /s /q %STAGING%\01_Source_Code\.vscode

:: Copy report and slides
copy /y "C:\Users\Admin\OneDrive\Report project.docx" %STAGING%\02_Report\ > nul
copy /y "C:\Users\Admin\OneDrive\FA26\PRO192\Private_Defense_Material\Report project.pdf" %STAGING%\02_Report\ > nul
copy /y "C:\Users\Admin\OneDrive\Project Presentation Slides (Optimized).pdf" %STAGING%\03_Presentation_Slides\"Project Presentation Slides.pdf" > nul

cd /d "C:\Users\Admin\OneDrive"
%RAR% a -r -y -m5 "PRO192_Group4_LibraryManagement.rar" "PRO192_Group4_Submission"
copy /y "C:\Users\Admin\OneDrive\PRO192_Group4_LibraryManagement.rar" "C:\Users\Admin\OneDrive\FA26\PRO192\PRO192_Group4_LibraryManagement.rar" > nul

:: Clean up staging
rd /s /q %STAGING%

echo ====================================================
echo 4. Verification Check for Users/Admin in paths...
echo ====================================================
%RAR% l "C:\Users\Admin\OneDrive\PRO192_Group4_LibraryManagement.rar" | findstr /i "Users Admin"
if %ERRORLEVEL% equ 0 (
    echo [WARNING] Found Users/Admin path in archive!
) else (
    echo [SUCCESS] Archive verified! 100%% clean relative paths without Users/Admin.
)
echo.
echo [DONE] All files compiled and packaged successfully!
pause

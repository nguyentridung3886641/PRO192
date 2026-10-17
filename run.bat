@echo off
chcp 65001 >nul
set "JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
echo Đang biên dịch mã nguồn Java UTF-8...
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin BusinessObject\UI\*.java Core\Entities\*.java DataObjects\DAO\*.java Utilities\*.java
if %errorlevel% neq 0 (
    echo [LỖI] Biên dịch thất bại!
    pause
    exit /b %errorlevel%
)
echo Biên dịch thành công! Đang khởi chạy chương trình...
echo.
java -cp bin BusinessObject.UI.Program
pause

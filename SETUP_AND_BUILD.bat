@echo off
echo === Elytra Swap Mod - Setup ===
echo.

rem Проверяем Java
java -version >NUL 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ОШИБКА] Java не найдена!
    echo Скачай JDK 21 с https://adoptium.net и установи.
    pause
    exit /b 1
)

rem Проверяем gradle-wrapper.jar
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo [ОШИБКА] Файл gradle\wrapper\gradle-wrapper.jar не найден!
    echo.
    echo Скачай его вручную в браузере по ссылке:
    echo https://github.com/gradle/gradle/raw/refs/tags/v8.6.0/gradle/wrapper/gradle-wrapper.jar
    echo.
    echo И положи в папку: gradle\wrapper\
    echo.
    pause
    exit /b 1
)

rem Убиваем старые Gradle демоны
call gradlew.bat --stop >NUL 2>&1

echo Запускаю сборку...
echo (При первом запуске скачается Gradle 8.6 и зависимости Fabric - несколько минут)
echo.

call gradlew.bat build --no-daemon

if %ERRORLEVEL% equ 0 (
    echo.
    echo === ГОТОВО! ===
    echo Мод находится в: build\libs\elytra-swap-1.0.0.jar
    echo Скопируй его в папку mods вашего Minecraft.
) else (
    echo.
    echo === ОШИБКА СБОРКИ ===
    echo Посмотри лог выше.
)

pause

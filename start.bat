@echo off
setlocal
echo [BotSnow] Preparing Gradle 9.4.0 environment and downloading dependencies...
if exist gradlew.bat (
  call gradlew.bat --version
  call gradlew.bat --refresh-dependencies build -x test
) else (
  echo gradlew.bat not found. Falling back to system gradle.
  gradle --version
  gradle --refresh-dependencies build -x test
)
echo [BotSnow] Done.
pause

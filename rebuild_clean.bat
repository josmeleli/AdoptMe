@echo off
echo ========================================
echo LIMPIEZA Y RECONSTRUCCION DE ADOPTME
echo ========================================
echo.

echo [1/5] Eliminando carpetas build...
if exist "build" rd /s /q "build"
if exist "app\build" rd /s /q "app\build"
if exist ".gradle" rd /s /q ".gradle"
echo Carpetas build eliminadas.
echo.

echo [2/5] Ejecutando gradlew clean...
call gradlew.bat clean
echo.

echo [3/5] Invalidando cache de Gradle...
call gradlew.bat --stop
echo.

echo [4/5] Reconstruyendo proyecto...
call gradlew.bat assembleDebug
echo.

echo [5/5] COMPLETADO!
echo.
echo ========================================
echo Ahora ejecuta la app desde Android Studio
echo o usa: gradlew.bat installDebug
echo ========================================
pause


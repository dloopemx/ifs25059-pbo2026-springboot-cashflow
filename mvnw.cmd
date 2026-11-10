@echo off
setlocal
set MAVEN_VERSION=3.9.11
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  mvn %*
  exit /b %ERRORLEVEL%
)
set CACHE_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MAVEN_HOME=%CACHE_DIR%\apache-maven-%MAVEN_VERSION%
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
  set ARCHIVE=%CACHE_DIR%\maven.zip
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%CACHE_DIR%\maven.zip'; Expand-Archive -Path '%CACHE_DIR%\maven.zip' -DestinationPath '%CACHE_DIR%' -Force; Remove-Item '%CACHE_DIR%\maven.zip'"
  if errorlevel 1 exit /b 1
)
call "%MAVEN_HOME%\bin\mvn.cmd" -f "%~dp0pom.xml" %*
exit /b %ERRORLEVEL%

@echo off
set APP_HOME=%~dp0
if "%GRADLE_USER_HOME%"=="" set GRADLE_USER_HOME=%APP_HOME%.gradle

java -classpath "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*

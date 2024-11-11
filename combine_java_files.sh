#!/bin/bash

@echo off
setlocal enabledelayedexpansion

REM 프로젝트 디렉토리 설정
set "PROJECT_DIR=C:\Users\MADCAHN\Desktop\E-Commerce"
set "OUTPUT_FILE=C:\Users\MADCAHN\Desktop\E-Commerce\combined_java_project.txt"

REM 기존의 출력 파일이 있으면 삭제
if exist "%OUTPUT_FILE%" del "%OUTPUT_FILE%"

REM Java 소스 코드 및 애플리케이션 파일 결합 (src\main 하위의 모든 .java, application.properties, application.yml 파일)
echo Java 소스 코드 및 애플리케이션 파일을 결합합니다...
for /r "%PROJECT_DIR%\src\main" %%F in (*.java application.properties application.yml) do (
    if exist "%%F" (
        echo File: %%F >> "%OUTPUT_FILE%"
        echo -------- >> "%OUTPUT_FILE%"
        type "%%F" >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
        echo -------- >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
    )
)

REM Gradle 빌드 파일 및 Docker 관련 파일 결합 (프로젝트 루트의 build.gradle, settings.gradle, Dockerfile, docker-compose.yml)
echo Gradle 및 Docker 파일을 결합합니다...
for %%F in ("%PROJECT_DIR%\build.gradle" "%PROJECT_DIR%\settings.gradle" "%PROJECT_DIR%\Dockerfile" "%PROJECT_DIR%\docker-compose.yml") do (
    if exist "%%F" (
        echo File: %%F >> "%OUTPUT_FILE%"
        echo -------- >> "%OUTPUT_FILE%"
        type "%%F" >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
        echo -------- >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
    )
)

echo 모든 파일이 %OUTPUT_FILE%에 결합되었습니다.
pause

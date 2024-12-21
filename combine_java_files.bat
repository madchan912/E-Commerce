@echo off
setlocal enabledelayedexpansion

REM 프로젝트 최상위 디렉토리 설정
set "BASE_DIR=C:\Users\MADCAHN\Desktop\E-Commerce"
set "OUTPUT_FILE=%BASE_DIR%\combined_project.txt"

REM 기존의 출력 파일이 있으면 삭제
if exist "%OUTPUT_FILE%" del "%OUTPUT_FILE%"

REM 전체 디렉토리와 파일을 순회하며 결합
echo 디렉토리 전체를 순회하며 파일을 결합합니다...
for /r "%BASE_DIR%" %%F in (*.java application.properties application.yml build.gradle settings.gradle Dockerfile docker-compose.yml) do (
    if exist "%%F" (
        echo File: %%F >> "%OUTPUT_FILE%"
        echo -------- >> "%OUTPUT_FILE%"
        type "%%F" >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
        echo -------- >> "%OUTPUT_FILE%"
        echo. >> "%OUTPUT_FILE%"
    )
)

echo 모든 파일이 %OUTPUT_FILE%에 결합되었습니다.
pause

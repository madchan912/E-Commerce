#!/bin/bash

# 프로젝트 디렉토리 및 출력 파일 경로 설정
PROJECT_DIR="/Users/madchan/E-Commerce"
OUTPUT_FILE="/Users/madchan/E-Commerce/combined_project.txt"

# 기존 출력 파일이 있으면 삭제
if [ -f "$OUTPUT_FILE" ]; then
    rm "$OUTPUT_FILE"
fi

# 모든 디렉토리 순회하며 파일 결합
echo "디렉토리를 순회하며 파일을 결합합니다..."

find "$PROJECT_DIR" -type f \( \
    -name "*.java" \
    -o -name "application.properties" \
    -o -name "application.yml" \
    -o -name "build.gradle" \
    -o -name "settings.gradle" \
    -o -name "Dockerfile" \
    -o -name "docker-compose.yml" \
\) | while read -r FILE; do
    echo "File: $FILE" >> "$OUTPUT_FILE"
    echo "--------" >> "$OUTPUT_FILE"
    cat "$FILE" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    echo "--------" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
done

echo "모든 파일이 $OUTPUT_FILE에 결합되었습니다."

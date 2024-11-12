
#!/bin/bash

# 프로젝트 디렉토리 및 출력 파일 경로 설정
PROJECT_DIR="/Users/madchan/Project/E-Commerce"
OUTPUT_FILE="/Users/madchan/Project/E-Commerce/combined_java_project.txt"

# 기존 출력 파일이 있으면 삭제
if [ -f "$OUTPUT_FILE" ]; then
    rm "$OUTPUT_FILE"
fi

# Java 소스 코드 및 애플리케이션 파일 결합
echo "Java 소스 코드 및 애플리케이션 파일을 결합합니다..."
find "$PROJECT_DIR/src/main" -type f \( -name "*.java" -o -name "application.properties" -o -name "application.yml" \) | while read -r FILE; do
    if [ -f "$FILE" ]; then
        echo "File: $FILE" >> "$OUTPUT_FILE"
        echo "--------" >> "$OUTPUT_FILE"
        cat "$FILE" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "--------" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
    fi
done

# Gradle 및 Docker 관련 파일 결합
echo "Gradle 및 Docker 파일을 결합합니다..."
for FILE in "$PROJECT_DIR/build.gradle" "$PROJECT_DIR/settings.gradle" "$PROJECT_DIR/Dockerfile" "$PROJECT_DIR/docker-compose.yml"; do
    if [ -f "$FILE" ]; then
        echo "File: $FILE" >> "$OUTPUT_FILE"
        echo "--------" >> "$OUTPUT_FILE"
        cat "$FILE" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "--------" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
    fi
done

echo "모든 파일이 $OUTPUT_FILE에 결합되었습니다."

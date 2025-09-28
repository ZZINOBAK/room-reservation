# 실행 런타임만 담은 가벼운 이미지
FROM eclipse-temurin:17-jre  
WORKDIR /app

# 로컬에서 만든 JAR를 컨테이너로 복사
# 파일명은 실제 빌드 산출물 이름으로 바꿔줘야 함
COPY build/libs/room-reservation-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]


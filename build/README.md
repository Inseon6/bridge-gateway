# Docker 배포 방법
## 한솔인티큐브 개발계 서버 정보
- IP : 10.1.14.175
- ID : dooly
- Password : 486
## Event Listener 애플리케이션 경로
/app/event-listener
## 디렉토리 트리
```text
[root@glab-framework1 event-listener]# ls -lthi
total 23M
102988553 -rw-rw-r-- 1 dooly dooly 468 Jul  5 10:51 Dockerfile  --> Docker Image 빌드에 필요한 파일
102988554 -rw-rw-r-- 1 dooly dooly 248 Jul  5 10:50 run.sh  --> Docker Container 시작 명령어 실행 파일
102963419 -rw-rw-r-- 1 dooly dooly 23M Jul  5 10:33 event-listener-v1.0.jar  --> Event Listener 실행 파일
102988555 -rw-rw-r-- 1 dooly dooly  59 Jul  5 10:15 build.sh  --> Docker Image 빌드 명령어 실행 파일
102988532 -rw-rw-r-- 1 dooly dooly  47 Jul  4 22:44 README
102963422 drwxrwxr-x 2 dooly dooly  43 Jul  4 22:13 etc  --> Event Listener 설정 파일 디렉토리
```
## run.sh
```shell
#!/bin/bash
docker run --name event-listener \  --> 1
-d \  --> 2
-p 6455:6455 \  --> 3
--mount type=bind,source=/logs/event-listener,target=/logs/event-listener \  --> 4
--mount type=bind,source=/app/event-listener/etc,target=/app/event-listener/etc \  --> 5
event-listener:1.0-dev  --> 6
```
1. 컨테이너 이름 지정
2. 컨테이너를 데몬으로 실행
3. 로컬 호스트 포트 6455로 인입한 패킷을 이벤트 리스너 컨테이너의 6455 포트로 포워딩
4. 로컬 호스트의 /logs/event-listener 경로와 컨테이너 내부의 /logs/event-listener 경로를 바인딩
5. 로컬 호스트의 /app/event-listener/etc 경로와 컨테이너 내부의 /app/event-listener/etc 경로를 바인딩
6. 컨테이너를 실행시키는데 사용할 이미지

## Dockerfile
```shell
# Base Image
FROM openjdk:8-jdk-alpine

# Set Time
ENV TZ=Asia/Seoul
RUN apk --no-cache add tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone \
    apk del tzdata

# Set Working Directory
RUN mkdir -p /app/event-listener
WORKDIR /app/event-listener

# Copy Jar File into Docker Container
COPY ./event-listener-v1.0.jar ./event-listener-v1.0.jar

# Start Event Listener
ENTRYPOINT ["java", "-jar", "./event-listener-v1.0.jar"]
```

## build.sh
```shell
docker build --rm -t event-listener:1.0-dev ./
```

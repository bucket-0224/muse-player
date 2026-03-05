# 베이스 이미지로 Node.js LTS 버전 사용
FROM node:18

# 컨테이너 내 작업 디렉토리 설정
WORKDIR /usr/src/youtube-dl-server

# 종속성 정의 복사
COPY package*.json ./

# 종속성 설치
RUN npm install

# 앱 소스 전체 복사
COPY . .

# 컨테이너가 열어야 할 포트
EXPOSE 5858

# 앱 실행 명령
CMD ["npm", "start"]

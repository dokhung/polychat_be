# Polychat 백엔드

Java 17, Spring Boot, PostgreSQL 16을 사용합니다.

## 환경 설정

백엔드 디렉터리에서 `.env.example`을 `.env`로 복사합니다.

```sh
cp .env.example .env
```

`.env`의 DB 비밀번호와 `JWT_SECRET`을 설정합니다. JWT 키는 32자 이상이어야 하며,
`openssl rand -hex 32`로 생성할 수 있습니다. `.env`는 Git과 Docker 빌드에서 제외됩니다.
Spring Boot도 작업 디렉터리의 `.env`를 자동으로 읽습니다. 값은 따옴표 없이 `KEY=value`로 작성합니다.

## Docker로 전체 실행

Docker Desktop을 실행한 뒤 다음 명령을 사용합니다. 로컬 Java 설치는 필요하지 않습니다.

```sh
docker compose up -d --build --wait
docker compose logs -f backend
```

- 백엔드: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- API 문서: http://localhost:8080/v3/api-docs

`POSTGRES_PORT`와 `SERVER_PORT`로 호스트 포트를 변경할 수 있습니다.
프런트엔드의 기본 API 주소는 `http://localhost:8080/api`입니다.
백엔드 포트를 변경하면 프런트엔드의 `VITE_API_URL`도 맞춰야 합니다.

```sh
docker compose stop
```

중지해도 PostgreSQL 데이터는 볼륨에 유지됩니다. 기존 DB 볼륨이 있다면 `.env`에
그 DB의 비밀번호를 사용해야 합니다. 환경 변수만 바꿔도 기존 DB 비밀번호가 변경되지는 않습니다.

## DB만 Docker로, 백엔드는 로컬 실행

Java 17이 필요합니다. 전체 Docker 실행에서 전환한다면 백엔드 컨테이너를 먼저 중지합니다.

```sh
docker compose stop backend
docker compose up -d --wait postgres
./gradlew bootRun
```

## 빌드 및 테스트

```sh
./gradlew clean build
```

테스트는 PostgreSQL 호환 모드의 메모리 H2 DB를 사용하므로 Docker나 `.env` 없이도 실행됩니다.
빌드된 JAR는 `build/libs/polychat_be-0.0.1-SNAPSHOT.jar`입니다.
DB가 실행 중일 때 백엔드 디렉터리에서 `java -jar build/libs/polychat_be-0.0.1-SNAPSHOT.jar`로 실행할 수도 있습니다.

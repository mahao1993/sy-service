# sy-service

Spring Boot service that reads MySQL and exposes an API to query article title, content, and publish time by `id`.

## Package structure

```text
com.xiaoluo.syservice
+-- article
|   +-- controller   // API entrypoints
|   +-- dto          // request/response models
|   +-- exception    // business exceptions
|   +-- repository   // JDBC data access
|   \-- service      // business services
+-- common
|   +-- exception    // global exception handling
|   \-- response     // shared response models
\-- security
    \-- apikey       // API Key auth filter and properties
```

## API

- Method: `GET`
- Path: `/api/articles/{id}`
- Auth header: `X-API-Key: <your-api-key>`
- 招投标数据查询API：`docs/customer-api-guide.md`

### Success response

```json
{
  "id": 1,
  "title": "example title",
  "content": "example content",
  "publishTime": "2026-03-19 10:30:00"
}
```

## Configuration

The service starts with the `dev` profile by default. Shared settings live in `application.yml`, and MySQL settings are only defined in profile files.

- `dev`: configured in `application-dev.yml`
- `prod`: configured in `application-prod.yml`

| Variable | Default | Description |
| --- | --- | --- |
| `DB_HOST` | profile-specific | MySQL host |
| `DB_PORT` | profile-specific | MySQL port |
| `DB_NAME` | profile-specific | MySQL database name |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `sanyangdbPootPass123!` | MySQL password |
| `API_KEY_HEADER` | `X-API-Key` | Header name for API key |
| `API_KEY` | `change-me` | API key expected by the server |
| `SERVER_PORT` | `8080` | Service port, and `start-prod.sh` defaults it to `80` when unset |

## Logging

- `info.log`: records `INFO`, `WARN`, and `ERROR`
- `error.log`: records `ERROR` only
- In `prod`, log files are written to the `logs/` directory beside the jar
- Historical logs are rolled by date and compressed to `logs/archive/*.log.gz`
- If a single day's log grows too large, it is further split by size within that date

## Dev run

```powershell
$env:JAVA_HOME="D:\JDP\jdk-17"
$env:API_KEY="your-api-key"
mvn spring-boot:run
```

## Prod run

```powershell
$env:JAVA_HOME="D:\JDP\jdk-17"
$env:API_KEY="your-api-key"
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Prod script

```powershell
$env:API_KEY="your-api-key"
.\scripts\start-prod.ps1
```

Optional flags:

- `-SkipBuild`: start from the existing jar under `target`
- `-Foreground`: run in the current terminal instead of background
- `-JavaHome "D:\JDP\jdk-17"`: override `JAVA_HOME`

## Prod shell script

```bash
mkdir -p /service/sy-service
cp sy-service-0.0.1-SNAPSHOT.jar /service/sy-service/
chmod +x ./scripts/start-prod.sh
./scripts/start-prod.sh
```

The shell script starts on port `80` by default. Override it when needed:

```bash
SERVER_PORT=8080 ./scripts/start-prod.sh
```

Optional flags:

- `--foreground`: run in the current terminal
- `--app-home /service/sy-service`: override application home
- `--java-home /opt/jdk-17`: override `JAVA_HOME`
- `--api-key your-api-key`: pass API key directly if you want to override the configured default

## Example request

```powershell
curl -H "X-API-Key: your-api-key" http://localhost:8080/api/articles/1
```

#!/usr/bin/env bash

set -euo pipefail

APP_HOME_ARG="${APP_HOME:-/service/sy-service}"
JAVA_HOME_ARG="${JAVA_HOME:-}"
API_KEY_ARG="${API_KEY:-}"
FOREGROUND="false"
JAR_NAME="sy-service-0.0.1-SNAPSHOT.jar"

usage() {
  cat <<'EOF'
Usage: ./scripts/start-prod.sh [options]

Options:
  --app-home <path>    Override application home, default: /service/sy-service
  --java-home <path>   Override JAVA_HOME
  --api-key <value>    Set API_KEY for the process
  --foreground         Run in the current terminal
  --help               Show this help message
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --app-home)
      APP_HOME_ARG="${2:-}"
      shift 2
      ;;
    --java-home)
      JAVA_HOME_ARG="${2:-}"
      shift 2
      ;;
    --api-key)
      API_KEY_ARG="${2:-}"
      shift 2
      ;;
    --foreground)
      FOREGROUND="true"
      shift
      ;;
    --help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage
      exit 1
      ;;
  esac
done

APP_HOME="${APP_HOME_ARG}"
RUN_DIR="${APP_HOME}/run"
PID_FILE="${RUN_DIR}/sy-service.pid"
JAR_FILE="${APP_HOME}/${JAR_NAME}"

mkdir -p "${RUN_DIR}"

if [[ -n "${JAVA_HOME_ARG}" ]]; then
  JAVA_CMD="${JAVA_HOME_ARG}/bin/java"
else
  JAVA_CMD="$(command -v java || true)"
fi

if [[ -z "${JAVA_CMD}" || ! -x "${JAVA_CMD}" ]]; then
  echo "java was not found. Set JAVA_HOME or pass --java-home." >&2
  exit 1
fi

if [[ ! -d "${APP_HOME}" ]]; then
  echo "Application home does not exist: ${APP_HOME}" >&2
  exit 1
fi

if [[ ! -f "${JAR_FILE}" ]]; then
  echo "Jar file was not found: ${JAR_FILE}" >&2
  echo "Please upload D:\\code\\xiaoluo\\sy-service\\target\\sy-service-0.0.1-SNAPSHOT.jar to ${APP_HOME}/" >&2
  exit 1
fi

if [[ -n "${API_KEY_ARG}" ]]; then
  export API_KEY="${API_KEY_ARG}"
fi
if [[ -n "${JAVA_HOME_ARG}" ]]; then
  export JAVA_HOME="${JAVA_HOME_ARG}"
fi
export SERVER_PORT="${SERVER_PORT:-80}"

if [[ -f "${PID_FILE}" ]]; then
  EXISTING_PID="$(tr -d '[:space:]' < "${PID_FILE}")"
  if [[ -n "${EXISTING_PID}" ]] && kill -0 "${EXISTING_PID}" 2>/dev/null; then
    echo "sy-service is already running with PID ${EXISTING_PID}. Stop it first or remove ${PID_FILE} if it is stale." >&2
    exit 1
  fi
  rm -f "${PID_FILE}"
fi

JAR_DIR="$(cd "$(dirname "${JAR_FILE}")" && pwd)"
LOG_DIR="${JAR_DIR}/logs"
STDOUT_LOG="${LOG_DIR}/sy-service.out.log"
STDERR_LOG="${LOG_DIR}/sy-service.err.log"

mkdir -p "${LOG_DIR}"

JAVA_ARGS=(
  "-Dfile.encoding=UTF-8"
  "-DLOG_HOME=${LOG_DIR}"
  "-jar"
  "${JAR_FILE}"
  "--spring.profiles.active=prod"
)

if [[ "${FOREGROUND}" == "true" ]]; then
  echo "Starting sy-service in foreground with prod profile..."
  cd "${APP_HOME}"
  exec "${JAVA_CMD}" "${JAVA_ARGS[@]}"
fi

echo "Starting sy-service in background with prod profile..."
nohup "${JAVA_CMD}" "${JAVA_ARGS[@]}" >> "${STDOUT_LOG}" 2>> "${STDERR_LOG}" &
APP_PID=$!
echo "${APP_PID}" > "${PID_FILE}"

echo "Started sy-service."
echo "PID: ${APP_PID}"
echo "STDOUT: ${STDOUT_LOG}"
echo "STDERR: ${STDERR_LOG}"
echo "PID file: ${PID_FILE}"

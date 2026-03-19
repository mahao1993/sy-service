#!/usr/bin/env bash

set -euo pipefail

JAVA_HOME_ARG="${JAVA_HOME:-}"
API_KEY_ARG="${API_KEY:-}"
SKIP_BUILD="false"
FOREGROUND="false"

usage() {
  cat <<'EOF'
Usage: ./scripts/start-prod.sh [options]

Options:
  --java-home <path>   Override JAVA_HOME
  --api-key <value>    Set API_KEY for the process
  --skip-build         Start from the existing jar under target
  --foreground         Run in the current terminal
  --help               Show this help message
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --java-home)
      JAVA_HOME_ARG="${2:-}"
      shift 2
      ;;
    --api-key)
      API_KEY_ARG="${2:-}"
      shift 2
      ;;
    --skip-build)
      SKIP_BUILD="true"
      shift
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

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
TARGET_DIR="${PROJECT_ROOT}/target"
LOG_DIR="${PROJECT_ROOT}/logs"
RUN_DIR="${PROJECT_ROOT}/run"
PID_FILE="${RUN_DIR}/sy-service.pid"
STDOUT_LOG="${LOG_DIR}/sy-service.out.log"
STDERR_LOG="${LOG_DIR}/sy-service.err.log"
DEFAULT_JAR="${TARGET_DIR}/sy-service-0.0.1-SNAPSHOT.jar"

mkdir -p "${LOG_DIR}" "${RUN_DIR}"

if [[ -n "${JAVA_HOME_ARG}" ]]; then
  JAVA_CMD="${JAVA_HOME_ARG}/bin/java"
else
  JAVA_CMD="$(command -v java || true)"
fi

if [[ -z "${JAVA_CMD}" || ! -x "${JAVA_CMD}" ]]; then
  echo "java was not found. Set JAVA_HOME or pass --java-home." >&2
  exit 1
fi

if [[ -z "${API_KEY_ARG}" || "${API_KEY_ARG}" == "change-me" ]]; then
  echo "API_KEY is required for prod startup. Export API_KEY or pass --api-key." >&2
  exit 1
fi

export API_KEY="${API_KEY_ARG}"
if [[ -n "${JAVA_HOME_ARG}" ]]; then
  export JAVA_HOME="${JAVA_HOME_ARG}"
fi

if [[ -f "${PID_FILE}" ]]; then
  EXISTING_PID="$(tr -d '[:space:]' < "${PID_FILE}")"
  if [[ -n "${EXISTING_PID}" ]] && kill -0 "${EXISTING_PID}" 2>/dev/null; then
    echo "sy-service is already running with PID ${EXISTING_PID}. Stop it first or remove ${PID_FILE} if it is stale." >&2
    exit 1
  fi
  rm -f "${PID_FILE}"
fi

if [[ "${SKIP_BUILD}" != "true" || ! -f "${DEFAULT_JAR}" ]]; then
  if ! command -v mvn >/dev/null 2>&1; then
    echo "mvn was not found and no jar is available under target. Build the project first or install Maven." >&2
    exit 1
  fi

  echo "Packaging application..."
  (
    cd "${PROJECT_ROOT}"
    mvn -B -DskipTests package
  )
fi

JAR_FILE="$(find "${TARGET_DIR}" -maxdepth 1 -type f -name 'sy-service-*.jar' ! -name '*.original' | head -n 1)"
if [[ -z "${JAR_FILE}" ]]; then
  echo "No runnable jar was found under ${TARGET_DIR}." >&2
  exit 1
fi

JAVA_ARGS=(
  "-Dfile.encoding=UTF-8"
  "-jar"
  "${JAR_FILE}"
  "--spring.profiles.active=prod"
)

if [[ "${FOREGROUND}" == "true" ]]; then
  echo "Starting sy-service in foreground with prod profile..."
  cd "${PROJECT_ROOT}"
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

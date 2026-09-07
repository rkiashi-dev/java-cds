#!/usr/bin/env bash
set -uo pipefail

code_to_status() {
  case "$1" in
    0) echo "SUCCESS" ;;
    1) echo "ERROR" ;;
    2) echo "WARNING" ;;
    3) echo "FAILURE" ;;
    *) echo "UNKNOWN" ;;
  esac
}

fetch_task_metadata() {
  if [ -n "${ECS_CONTAINER_METADATA_URI_V4:-}" ] && command -v curl >/dev/null 2>&1; then
    curl -s "${ECS_CONTAINER_METADATA_URI_V4}/task" 2>/dev/null
  fi
}

check_log_driver() {
  local ts
  ts=$(date -u +%Y-%m-%dT%H:%M:%SZ)

  if [ -z "${ECS_CONTAINER_METADATA_URI_V4:-}" ] || ! command -v curl >/dev/null 2>&1; then
    printf '{"time":"%s","tag":"exit-report","level":"WARN","message":"cannot verify log driver: metadata endpoint or curl unavailable"}\n' "${ts}" >&2
    return
  fi

  local log_driver
  log_driver=$(curl -s "${ECS_CONTAINER_METADATA_URI_V4}" 2>/dev/null | grep -o '"LogDriver":"[^"]*"' | cut -d'"' -f4)

  if [ "${log_driver}" != "awslogs" ]; then
    printf '{"time":"%s","tag":"exit-report","level":"WARN","message":"logDriver is not awslogs (actual: %s); exit-report logs may not reach CloudWatch Logs"}\n' \
      "${ts}" "${log_driver:-unknown}" >&2

    if [ "${REQUIRE_AWSLOGS:-false}" = "true" ]; then
      printf '{"time":"%s","tag":"exit-report","level":"FATAL","message":"REQUIRE_AWSLOGS=true but logDriver is not awslogs; aborting startup"}\n' "${ts}" >&2
      exit 3
    fi
  fi
}

child_pid=""
term_handler() {
  if [ -n "${child_pid}" ]; then
    kill -TERM "${child_pid}" 2>/dev/null || true
  fi
}
trap term_handler SIGTERM SIGINT

check_log_driver

JAVA_OPTS="${JAVA_OPTS:--XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -XX:MaxRAMPercentage=75.0}"

# shellcheck disable=SC2086
java ${JAVA_OPTS} "$@" &
child_pid=$!

wait "${child_pid}"
exit_code=$?

status=$(code_to_status "${exit_code}")
timestamp=$(date -u +%Y-%m-%dT%H:%M:%SZ)

task_metadata=$(fetch_task_metadata)
task_arn=$(printf '%s' "${task_metadata}" | grep -o '"TaskARN":"[^"]*"' | head -1 | cut -d'"' -f4)
task_cpu=$(printf '%s' "${task_metadata}" | grep -o '"CPU":[0-9.]*' | head -1 | cut -d':' -f2)
task_memory=$(printf '%s' "${task_metadata}" | grep -o '"Memory":[0-9]*' | head -1 | cut -d':' -f2)

task_arn="${task_arn:-unknown}"
task_cpu="${task_cpu:-null}"
task_memory="${task_memory:-null}"

printf '{"time":"%s","tag":"exit-report","status":"%s","exitCode":%d,"taskArn":"%s","taskCpuVcpu":%s,"taskMemoryMiB":%s}\n' \
  "${timestamp}" "${status}" "${exit_code}" "${task_arn}" "${task_cpu}" "${task_memory}"

exit "${exit_code}"

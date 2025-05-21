#!/usr/bin/env bash
set -e

source scripts/ci/third-party-update/version_grep_regex.sh

LATEST_VERSION_PLUGIN=$(./scripts/ci/third-party-update/get_latest_github_version.sh "tbroyer/gradle-errorprone-plugin")
LATEST_VERSION_TOOL=$(./scripts/ci/third-party-update/get_latest_github_version.sh "google/error-prone")

# Modified line to strip any 'v' prefix from both variables
echo "${LATEST_VERSION_PLUGIN#v} ${LATEST_VERSION_TOOL#v}"

#!/usr/bin/env bash
set -e

declare -r DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && echo $PWD)"
declare -r UPLOADER="build/libs/mongo-uploader.jar"
declare -r MONGO="${1:?Expected mongo uri as first param}"
declare -r ADDRESS="${2:?Expected address to resolve}"

if [ ! -f "$UPLOADER" ]; then
  echo "File not found: mongo-uploader.jar"
  echo "Exiting"
  exit 1
fi

java -jar "$UPLOADER" \
  -m "$MONGO" \
  -r "$ADDRESS"

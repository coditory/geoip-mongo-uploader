#!/usr/bin/env bash
set -e

declare -r DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && echo $PWD)"
declare -r UPLOADER="build/libs/mongo-uploader.jar"
declare -r MONGO="${1:?Expected mongo uri as first param}"

./gradlew build

if [ ! -f "$UPLOADER" ]; then
  echo "File not found: mongo-uploader.jar"
  echo "Exiting"
  exit 1
fi

if [ ! -d "geolite" ]; then
  declare -r GEO_LICENSE="${2:?Expected geoip license key as third param}"
  (cd $(mktemp -d) &&
    wget -O geolite.zip "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City-CSV&license_key=$GEO_LICENSE&suffix=zip" &&
    unzip -j geolite.zip -d "$DIR/geolite")
fi

java -jar "$UPLOADER" \
  -m "$MONGO" \
  -d "$DIR/geolite"

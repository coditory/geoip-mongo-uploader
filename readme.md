# Mongo Uploader

Uploads GeuIp data to MongoDB.

GeoIp data can be fetched from [maxmind.com](https://www.maxmind.com/).
It requires registering a free account.

# Sample usage

Using jar:
```sh
# Print help
java -jar mongo-uploader.jar -h

# Upload GeoLite CSV to MongoDB
java -jar geoip-mongo-uploader.jar \
    --mongo mongo://localhost:27017/testDb
    --dir ~/Desktop/geoip
# Response:
# Lot of logs informing abut the progress

# Print geoip location using MongoDB
java -jar geoip-mongo-uploader.jar \
    --mongo mongo://localhost:27017/testDb
    --resolve 5.133.248.237
# Response:
# Document{{_id=3088171, continentCode=EU, cityName=Poznan, timeZone=Europe/Warsaw, countryIsoCode=PL}}
```

Using scripts:
```sh
# Fetch GeoIp Lite CSV version and uploads it to MongoDB 
./upload.sh mongodb://localhost/test "${LICENSE_KEY}"

# Resolve address using MongoDB 
./resolve.sh mongodb://localhost/test 5.133.248.237
```
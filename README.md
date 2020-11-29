## Requirements
* Redis
* Firebase account with the following services configured:
    * Cloud Firestore
    * Cloud Messaging

## Configuration
Required environment variables:

| Variable | Description | Example value | 
| -------- | ----------- | ------------- |
| REDIS_HOST | Address of the redis host | localhost |
| REDIS_PORT | Port of the redis host | 6379 |
| OPENSKY_API_URL | OpenSky API url | https://opensky-network.org/api/states/all |
| OPENSKY_API_USER | OpenSky API user | user |
| OPENSKY_API_PASSWORD | OpenSky API password | password |
| GOOGLE_CONFIG_PATH | Path to Firebase config file | "/home/myplanes/.myplanes/myplanes-aef4f-firebase-adminsdk-z1qhu-80e7a918bb.json" |
| GOOGLE_CONFIG_DATABASE_URL | URL to Firestore | https://myplanes-aef4f.firebaseio.com |
| AIRCRAFT_DATA_FILE | File containing aircraft data |  "/home.myplanes/.myplanes/aircraftDatabase.csv" |
| FRONTEND_URL | Address of the frontend app | https://myplanes.cloudfront.net |
| SSL_ENABLED | Should enable SSL | use false on localhost and true for production |

When SSL enabled is enabled, also set these variables:

| Variable | Description | Example value | 
| -------- | ----------- | ------------- |
| SSL_CERT_PATH | Path to SSL certificate file | "/etc/letsencrypt/live/myplanes.it/server.p12" |
| SSL_CERT_PASSWORD | Password to the certificate | password |
| KEYSTORE_TYPE | Type of keystore | PKCS12 |

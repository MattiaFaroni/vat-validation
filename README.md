<h1 align="center">VAT-Validation</h1>

<p align="center">
<img width="120" src="./img/vat-validation-logo.png" alt=""/>
</p>

<p align="center">
<img src="https://img.shields.io/badge/Java-ED7B09?style=for-the-badge&logo=openjdk&logoColor=white" alt="">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="">
<img src="https://img.shields.io/badge/Sentry-black?style=for-the-badge&logo=Sentry&logoColor=#362D59" alt="">
<img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white" alt="">
</p>

--------

VAT-Validation is a Java-based application designed to validate VAT numbers through the VIES (VAT Information Exchange System) web service API. The project is implemented using Java 17, built with Gradle, and deployed on Apache Tomcat 10.  
The application leverages Sentry for real-time exception tracking and monitoring, while Redis is integrated as a caching layer to optimize performance by reducing redundant API calls.

## Features
- **VAT Number Validation**: Performs real-time validation of VAT numbers via the VIES API.
- **Service and Country Status Check**: Verifies the operational status of VIES services and lists ISO country codes currently enabled for VAT number validation.
- **API Health Check Endpoint**: Exposes a health check endpoint to monitor the availability and operational status of the application and its dependencies.

## Configuration
For proper operation of the API, specific configuration parameters must be defined within the api-settings.json file.
The required parameters are detailed below:


```json
{
  "vies": {
    "checkVatNumberUrl": "VIES url to validate a VAT number (already configured)",
    "checkStatusUrl": "VIES url to check service status (already configured)"
  },
  "sentry": {
    "dsn": "Sentry dsn"
  },
  "redis": {
    "host": "Redis host",
    "port": "Redis port"
  }
}
```

The configuration file can be bundled within the application’s .war package or placed externally in the config directory of the Tomcat server.
At runtime, the application prioritizes the external file located in the Tomcat config directory; if not found, it falls back to the internal version packaged within the .war.

## Validation Service
The validation service performs VAT number verification and, when available, returns the corresponding entity details registered in the VIES system.
The request must include a JSON body with the required parameters as shown below. Both fields are mandatory for successful processing.

```http request
POST /api/vies/check
```


```body
{
    "iso2": "IT",
    "vatNumber": "00159560366"
}
```

Below is an example of a service response:

```json
{
    "data": {
        "iso2": "IT",
        "vatNumber": "00159560366",
        "valid": true,
        "name": "FERRARI S.P.A.",
        "address": "VIA EMILIA EST 1163 41122 MODENA MO"
    },
    "system": {
        "code": 0,
        "description": "Ok"
    }
}
```

## Status Service
The status service provides real-time information on the availability of VIES services and returns the list of ISO country codes currently supported for VAT number validation.

```http request
GET /api/vies/status
```

Below is an example of a service response:

```json
{
  "data": [
    {
      "iso2": "AT",
      "status": "Available"
    },
    {
      "iso2": "BE",
      "status": "Unavailable"
    },
    "..."
  ],
  "system": {
    "code": 0,
    "description": "Ok"
  }
}
```

## Health Check Service
The health check service verifies the operational status of the API.
As part of the check, the API also performs a request to the VIES system to ensure it is reachable and responding as expected.
```http request
GET /api/healthcheck
```

If the API works correctly, it will return code 200, otherwise code 417.

## Redis cache cleaner Service
The Redis cache cleaner service provides functionality to remove specific cache keys or to clear the entire cache.
This service is essential when data has been modified over time, and there is a need to update or refresh the stored information within the Redis cache.
```http request
DELETE /api/cache/clear
```

```body
{
    "keys": [
        {
            "iso2": "IT",
            "vatNumber": "00159560366"
        }
    ]
}
```

If the body is empty, the entire Redis cache is cleared.  
Below is an example of a successful outcome:

```json
{
    "status": "SUCCESS",
    "message": "Cache cleared successfully"
}
```

In the event of an unsuccessful operation, the status will be updated to "ERROR," and the response will include an HTTP status code 500.
<h1 style="text-align: center;">VAT-Validation</h1>

<p style="text-align: center;">
<img width="120" src="./img/vat-validation-logo.png" alt=""/>
</p>

<p style="text-align: center;">
<img src="https://img.shields.io/badge/Java-ED7B09?style=for-the-badge&logo=openjdk&logoColor=white" alt="">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="">
<img src="https://img.shields.io/badge/Sentry-black?style=for-the-badge&logo=Sentry&logoColor=#362D59" alt="">
<img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white" alt="">
</p>

--------

VAT-Validation is a java project that uses the API provided by VIES to validate the VAT number.  
The project is developed using Java 17 with Gradle and Tomcat 10.  
Sentry is also used to catch exceptions and Redis to cache requests.

## Features
* Validation of a VAT number
* Check the status of VIES services and ISOs enabled for validation
* Health check request to check the status of the API

## Configuration
For the correct functioning of the API, it is necessary to configure some parameters inside the api-settings.json file.
The parameters to configure are listed below:

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

This configuration file can be saved inside the application's .war, otherwise inside the config folder of a Tomcat.  
The application will first search for the file inside the tomcat and if it does not find it, it reads the one contained in the application.

## Validation Service
The validation service allows you to check whether a VAT number is valid or not, and obtain information about it.  
The request requires a body (shown below), where both parameters are mandatory.

```http request
POST /api/vies/check
```


```body
{
    "iso2": "IT",
    "vatNumber": "00159560366"
}
```

Below is an example of a service response.

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
The status service is used to check whether VIES services are active and which ISOs are available.

```http request
GET /api/vies/status
```

Below is an example of a service response.

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
The health check service allows you to check if the API is working.  
The API also makes a call to VIES to verify that the service is up and running and responding correctly.

```http request
GET /api/healthcheck
```

If the API works correctly it will return a code 200, otherwise a code 417.
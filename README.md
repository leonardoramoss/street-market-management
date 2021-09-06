## Reference Documentation

### Dependencies
    JDK 11
    Kotlin
    Gradle
    PostgresSQL
    Docker
    IntelliJ (Recommended)

### Frameworks
- [ktor](https://ktor.io/) - Lightweight web framework
- [exposed](https://github.com/JetBrains/Exposed) - ORM
- [koin](https://insert-koin.io/) - Dependencies injection

## Documentation
[http://localhost:8080/docs](http://localhost:8080/docs/index.html)

## Running

#### Docker
- To run or project stack mounted the following commands:
   ```
   docker-compose build
   docker-compose up
   ```
#### IDE

- Running only dependencies and start application by IntelliJ
    ```
    docker-compose -f docker-compose-local.yml
    ```
  Make sure the environment file .env.local is configured in the IDE.
See: https://plugins.jetbrains.com/plugin/7861-envfile

The application will start at http://localhost:8080/

## Import data

In the `dataset` folder just run the `import.sh` script to do the local loading (Docker).

If you want to run in another environment, in that same folder after executing the script, an SQL script `streetmarket.sql will be generated, just run in any other environment

## API
#### Management endpoints

| Endpoint                  | HTTP Method   | Description                                        | 
| --------------------------| ------------- | -------------------------------------------------  |
| `/v1/market`              | POST          | Boarding new street market                         |
| `/v1/market/{register}`   | PUT           | Replace an existing street market                  |
| `/v1/market/{register}`   | DELETE        | Remove street market when exists                   |

#### Search endpoints
| Endpoint      | HTTP Method   | Description                                        | 
| ------------- | ------------- | -------------------------------------------------  |
| `/v1/market/{register}`   | GET          | Detail street market |
| `/v1/market/search`       | GET          | Search street markets |

## Examples

##### POST - /v1/market

```
curl --location --request POST '{{enviroment}}/v1/market' \
--header 'Content-Type: application/json' \
--data-raw 
'{
    "register": "4045-2",
    "name": "PRACA SANTA HELENA",
    "census": {
        "sector": 355030893000035,
        "weighting_area": 3550308005042,
        "district_code": 95,
        "district": "VILA PRUDENTE",
        "borough_code": 29,
        "borough": "VILA PRUDENTE",
        "region5": "Leste",
        "region8": "Leste 1"
    },
    "address": {
        "street": "RUA JOSE DOS REIS",
        "number": "909",
        "neighborhood": "VL ZELINA",
        "landmark": "RUA OLIVEIRA GOUVEIA"
    },
    "geolocation": {
        "long": -46574716,
        "lat": -23584852
    }
}'
```

##### PUT - /v1/market/{register}
```
curl --location --request PUT '{{enviroment}}/v1/market/4045-2' \
--header 'Content-Type: application/json' \
--data-raw '{
    "register": "4045-2",
    "name": "VILA FORMOSA",
    "census": {
        "sector": 355030885000091,
        "weighting_area": 3550308005040,
        "district_code": 87,
        "district": "VILA FORMOSA",
        "borough_code": 26,
        "borough": "ARICANDUVA-FORMOSA-CARRAO",
        "region5": "Leste",
        "region8": "Leste 1"
    },
    "address": {
        "street": "RUA MARAGOJIPE",
        "number": "S/N",
        "neighborhood": "VL FORMOSA",
        "landmark": "TV RUA PRETORIA"
    },
    "geolocation": {
        "long": -46550164,
        "lat": -23558733
    }
}'
```

##### DELETE - /v1/market/{register}
```
curl --location --request DELETE '{{enviroment}}/v1/market/4041-2'
```

##### GET - /v1/market/{register}
```
curl --location --request GET '{{enviroment}}/v1/market/4041-2'
```

####Response
```json
{
    "register": "4041-2",
    "name": "VILA FORMOSA",
    "census": {
        "sector": 355030885000091,
        "weighting_area": 3550308005040,
        "district_code": 87,
        "district": "VILA FORMOSA",
        "borough_code": 26,
        "borough": "ARICANDUVA-FORMOSA-CARRAO",
        "region5": "Leste",
        "region8": "Leste 1"
    },
    "address": {
        "street": "RUA MARAGOJIPE",
        "number": "S/N",
        "neighborhood": "VL FORMOSA",
        "landmark": "TV RUA PRETORIA"
    },
    "geolocation": {
        "long": -46550164,
        "lat": -23558733
    }
}
```
##### GET - /v1/market/search
```
curl --location --request GET '{{enviroment}}/v1/market/search?region5=Leste&size=10&page=0&neighborhood=VL FORMOSA&district=VILA FORMOSA&name=VILA FORMOSA'
```

#### Response
```json
{
    "page": 0,
    "size": 1,
    "total": 1,
    "filter": {
      "size": "10",
      "page": "0",
      "name": "VILA FORMOSA",
      "neighborhood": "VL FORMOSA",
      "district": "VILA FORMOSA",
      "region5": "Leste"
    },
    "data": [
        {
            "register": "4041-2",
            "name": "VILA FORMOSA",
            "census": {
                "sector": 355030885000091,
                "weighting_area": 3550308005040,
                "district_code": 87,
                "district": "VILA FORMOSA",
                "borough_code": 26,
                "borough": "ARICANDUVA-FORMOSA-CARRAO",
                "region5": "Leste",
                "region8": "Leste 1"
            },
            "address": {
                "street": "RUA MARAGOJIPE",
                "number": "S/N",
                "neighborhood": "VL FORMOSA",
                "landmark": "TV RUA PRETORIA"
            },
            "geolocation": {
                "long": -46550164,
                "lat": -23558733
            }
        },
        {
            "register": "4045-2",
            "name": "VILA FORMOSA",
            "census": {
                "sector": 355030885000091,
                "weighting_area": 3550308005040,
                "district_code": 87,
                "district": "VILA FORMOSA",
                "borough_code": 26,
                "borough": "ARICANDUVA-FORMOSA-CARRAO",
                "region5": "Leste",
                "region8": "Leste 1"
            },
            "address": {
                "street": "RUA MARAGOJIPE",
                "number": "S/N",
                "neighborhood": "VL FORMOSA",
                "landmark": "TV RUA PRETORIA"
            },
            "geolocation": {
                "lat": -23558733,
                "long": -46550164
            }
        }
    ]
}
```

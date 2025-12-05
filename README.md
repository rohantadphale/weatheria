# Weatheria

A simple Spring Boot application that provides current weather information for a given city.

## Project Overview

Weatheria is a RESTful web service that allows you to retrieve the current weather conditions for any city in the world. It uses the [Open-Meteo API](https://open-meteo.com/) for geocoding and weather forecasts.

## Installation

To run this project locally, you will need to have the following installed:

*   Java 25
*   Maven

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/your-username/weatheria.git
    ```

2.  **Navigate to the project directory:**

    ```bash
    cd weatheria
    ```

3.  **Build the project using Maven:**

    ```bash
    ./mvnw clean install
    ```

## Usage

Once the project is built, you can run the application using the following command:

```bash
java -jar target/weatheria-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080.

## Configuration

The application requires no special configuration. By default, it runs on port 8080. You can change the port by modifying the `src/main/resources/application.properties` file and adding the following line:

```properties
server.port = 8081
```

## API Reference

The following endpoints are available:

### Health Check

*   **GET** `/api/health`

    Returns the status of the application.

    **Success Response (200 OK):**

    ```json
    {
        "status": "ok"
    }
    ```

### Get Weather by City

*   **GET** `/api/weather?city={city}`

    Returns the current weather for the specified city.

    **URL Parameters:**

    *   `city` (required): The name of the city.

    **Success Response (200 OK):**

    ```json
    {
        "city": "London",
        "latitude": 51.5072,
        "longitude": -0.1276,
        "temperature": 15.0,
        "windspeed": 10.0,
        "weathercode": 0,
        "time": "2025-11-06T12:00:00Z"
    }
    ```

    **Weathercode Explanation:**

    The `weathercode` field uses the WMO (World Meteorological Organization) Code Table 4677 to describe current weather conditions. You can find the complete table [here](https://www.nodc.noaa.gov/archive/arc0021/0002199/1.1/data/0-data/HTML/WMO-CODE/WMO4677.HTM).

    Common weather codes include:

    *   `0`: Clear sky
    *   `1, 2, 3`: Mainly clear, partly cloudy, and overcast
    *   `45, 48`: Fog and depositing rime fog
    *   `51, 53, 55`: Drizzle: Light, moderate, and dense intensity
    *   `61, 63, 65`: Rain: Light, moderate, and heavy intensity
    *   `71, 73, 75`: Snow fall: Slight, moderate, and heavy intensity
    *   `80, 81, 82`: Rain showers: Slight, moderate, and violent
    *   `95`: Thunderstorm: Slight or moderate

    **Error Response (404 Not Found):**

    ```json
    {
        "error": "City not found"
    }
    ```

    **Error Response (502 Bad Gateway):**

    ```json
    {
        "error": "Geocoding API call failed"
    }
    ```

### OpenAPI Documentation

The OpenAPI documentation is available at `/swagger-ui.html`.

## Contributing Guidelines

Contributions are welcome! Please feel free to submit a pull request.

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/your-feature`).
3.  Make your changes.
4.  Commit your changes (`git commit -m 'Add some feature'`).
5.  Push to the branch (`git push origin feature/your-feature`).
6.  Open a pull request.

## License Information

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Changelog

### v0.0.1 (2025-11-06)

*   Initial release.



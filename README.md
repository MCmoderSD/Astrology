# Astrology

## Description

A utility for fetching daily astrology predictions.

## Usage

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>de.MCmoderSD</groupId>
        <artifactId>astrology</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### Usage Example
```java
import de.MCmoderSD.astrology.ProkeralaAPI;

import java.time.MonthDay;

public class Main {

    public static void main(String[] args) {

        // Load the client ID and client secret
        String clientId = "PROKERALA_CLIENT_ID";
        String clientSecret = "PROKERALA_CLIENT_SECRET";

        // Create a new ProkeralaAPI instance
        ProkeralaAPI api = new ProkeralaAPI(clientId, clientSecret);

        // Get the daily horoscope for Aries
        String horoscope = api.dailyPrediction(ProkeralaAPI.ZodiacSign.ARIES);
        System.out.println(horoscope);

        // Get the daily horoscope for Taurus
        MonthDay date = MonthDay.of(5, 4);
        horoscope = api.dailyPrediction(date);
        System.out.println(horoscope);
    }
}
```
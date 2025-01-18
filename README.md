# Astrology
[![](https://jitpack.io/v/MCmoderSD/Astrology.svg)](https://jitpack.io/#MCmoderSD/Astrology)


## Description
A utility for fetching daily astrology predictions.


## Usage

### Maven
Make sure you have the JitPack repository added to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add the dependency to your `pom.xml` file: 
```xml
<dependency>
    <groupId>com.github.MCmoderSD</groupId>
    <artifactId>Astrology</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Usage Example
```java
import de.MCmoderSD.astrology.data.DailyPrediction;
import de.MCmoderSD.astrology.enums.ZodiacSign;
import de.MCmoderSD.astrology.manager.Astrology;

import java.time.MonthDay;

public class Main {

    public static void main(String[] args) {

        // Load the client ID and client secret (you can also use multiple clients)
        String[] clientIds = new String[] {"YOUR_CLIENT_ID"};
        String[] clientSecrets = new String[] {"YOUR_CLIENT_SECRET"};

        // Initialize the API
        Astrology api = new Astrology(clientIds, clientSecrets);

        // Get the daily horoscope for Aries
        ZodiacSign zodiacSign = ZodiacSign.ARIES;
        DailyPrediction horoscope = api.dailyPrediction(zodiacSign);
        System.out.println(horoscope.getPrediction());

        // Get the daily horoscope for Taurus
        MonthDay date = MonthDay.of(5, 4);
        horoscope = api.dailyPrediction(date);
        System.out.println(horoscope.getPrediction());
    }
}
```
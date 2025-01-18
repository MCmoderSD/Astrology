package de.MCmoderSD.astrology.manager;

import de.MCmoderSD.astrology.core.ProkeralaAPI;
import de.MCmoderSD.astrology.data.DailyPrediction;
import de.MCmoderSD.astrology.enums.ZodiacSign;

import java.io.IOException;
import java.time.MonthDay;

/**
 * The {@code Astrology} class provides functionality for managing API clients
 * to retrieve daily horoscope predictions based on zodiac signs.
 * It supports multiple API clients and automatically handles API rate-limiting or blocking errors by switching clients.
 */
@SuppressWarnings("ALL")
public class Astrology {

    /**
     * Array of {@link ProkeralaAPI} instances used for retrieving daily predictions.
     */
    private final ProkeralaAPI[] apis;

    /**
     * The index of the current {@link ProkeralaAPI} being used.
     */
    private int apiIndex;

    /**
     * The number of API client swaps performed due to errors.
     */
    private int apiSwaps;

    /**
     * Constructs an {@code Astrology} instance with a single API client.
     *
     * @param clientId     the client ID for the Prokerala API.
     * @param clientSecret the client secret for the Prokerala API.
     */
    public Astrology(String clientId, String clientSecret) {

        // Initialize APIs
        apis = new ProkeralaAPI[] { new ProkeralaAPI(clientId, clientSecret) };

        // Initialize variables
        apiIndex = 0;
        apiSwaps = 0;
    }

    /**
     * Constructs an {@code Astrology} instance with multiple API clients.
     *
     * @param clientIds     an array of client IDs for the Prokerala API.
     * @param clientSecrets an array of client secrets for the Prokerala API.
     * @throws IllegalArgumentException if the number of client IDs and client secrets do not match.
     */
    public Astrology(String[] clientIds, String[] clientSecrets) {

        // Check arguments
        if (clientIds.length != clientSecrets.length) throw new IllegalArgumentException("The number of client IDs and client secrets must be equal.");

        // Initialize APIs
        apis = new ProkeralaAPI[clientIds.length];
        for (var i = 0; i < clientIds.length; i++) apis[i] = new ProkeralaAPI(clientIds[i], clientSecrets[i]);

        // Initialize variables
        apiIndex = 0;
        apiSwaps = 0;
    }

    /**
     * Retrieves the daily prediction for a specific date.
     *
     * @param monthDay the {@link MonthDay} for which the daily prediction is requested.
     * @return a {@link DailyPrediction} for the specified date.
     */
    public DailyPrediction dailyPrediction(MonthDay monthDay) {
        return dailyPrediction(ZodiacSign.getZodiacSign(monthDay));
    }

    /**
     * Retrieves the daily prediction for a specific zodiac sign.
     *
     * @param zodiacSign the {@link ZodiacSign} for which the daily prediction is requested.
     * @return a {@link DailyPrediction} for the specified zodiac sign.
     * @throws RuntimeException if all API clients are blocked or another error occurs.
     */
    public DailyPrediction dailyPrediction(ZodiacSign zodiacSign) {
        try {

            // Get horoscope
            return apis[apiIndex].dailyPrediction(zodiacSign);

        } catch (IOException | InterruptedException e) {

            // Handle error 403
            if (e.getMessage().concat(" ").contains("403")) {
                // Swap API
                if (apiSwaps >= apis.length) throw new RuntimeException("All API clients are blocked.");
                else {

                    // Swap API
                    apiSwaps++;
                    apiIndex++;
                    if (apiIndex >= apis.length) apiIndex = 0;

                    // Retry
                    return dailyPrediction(zodiacSign);
                }
            } else throw new RuntimeException("Failed to get daily prediction. " + e.getMessage());
        }
    }

    /**
     * Retrieves the array of {@link ProkeralaAPI} instances managed by this class.
     *
     * @return an array of {@link ProkeralaAPI} instances.
     */
    public ProkeralaAPI[] getAPIs() {
        return apis;
    }
}
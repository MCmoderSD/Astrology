package de.MCmoderSD.astrology;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import java.sql.Timestamp;

import java.time.Instant;
import java.time.MonthDay;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.util.Objects;

/**
 * A utility class for interacting with the Prokerala API to fetch horoscope predictions.
 * Supports fetching daily predictions based on Zodiac signs.
 */
@SuppressWarnings("ALL")
public class ProkeralaAPI {

    // Endpoints
    private static final String TOKEN_ENDPOINT = "https://api.prokerala.com/token";
    private static final String DAILY_PREDICTION_ENDPOINT = "https://api.prokerala.com/v2/horoscope/daily";

    // Constants
    private final String clientId;
    private final String clientSecret;

    // Variables
    private String accessToken;
    private Timestamp tokenExpiration;

    /**
     * Constructs a new ProkeralaAPI instance.
     *
     * @param clientId     The client ID for authenticating with the Prokerala API.
     * @param clientSecret The client secret for authenticating with the Prokerala API.
     */
    public ProkeralaAPI(String clientId, String clientSecret) {

        // Set the client ID and client secret
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        // Authenticate
        this.tokenExpiration = authenticate();
    }

    /**
     * Authenticates with the Prokerala API and retrieves an access token.
     *
     * @return A {@link Timestamp} representing the token expiration time.
     * @throws RuntimeException if authentication fails.
     */
    private Timestamp authenticate() {

        // Check if the token is expired
        if (accessToken == null || System.currentTimeMillis() >= Objects.requireNonNull(tokenExpiration).getTime()) {
            try {

                // Create a new connection to the token endpoint
                URI uri = new URI(TOKEN_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                // Create the request body
                String requestBody = String.format(
                        "grant_type=client_credentials" +
                                "&client_id=%s" +
                                "&client_secret=%s",
                        clientId, clientSecret);

                // Send the request
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestBody.getBytes());
                    os.flush();
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    // Parse the response
                    JSONObject jsonResponse = new JSONObject(new String(conn.getInputStream().readAllBytes()));

                    // Set the access token and expiration time
                    accessToken = jsonResponse.getString("access_token");
                    long expiresIn = jsonResponse.getLong("expires_in");

                    // Return the token expiration time
                    return tokenExpiration = new Timestamp(System.currentTimeMillis() + expiresIn * 1000);
                } else throw new RuntimeException("Failed to authenticate. Response code: " + conn.getResponseCode());
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException("Failed to authenticate. " + e.getMessage());
            }
        }
        return tokenExpiration;
    }

    /**
     * Fetches the daily horoscope prediction based on a given date.
     *
     * @param monthDay The {@link MonthDay} representing the date.
     * @return The daily prediction as a string.
     */
    public String dailyPrediction(MonthDay monthDay) {
        return dailyPrediction(ZodiacSign.getZodiacSign(monthDay));
    }

    /**
     * Fetches the daily horoscope prediction for a given Zodiac sign.
     *
     * @param sign The {@link ZodiacSign} for which the prediction is to be fetched.
     * @return The daily prediction as a string.
     * @throws RuntimeException if authentication or fetching the prediction fails.
     */
    public String dailyPrediction(ZodiacSign sign) {

        // Authenticate
        tokenExpiration = authenticate();
        if (accessToken == null) throw new RuntimeException("Failed to authenticate.");

        // Fetch the daily prediction
        String currentDatetime = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        String url = DAILY_PREDICTION_ENDPOINT + "?sign=" + sign.toString().toLowerCase() + "&datetime=" + currentDatetime;

        try {

            // Create a new connection to the API
            URI requestUrl = new URI(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return new JSONObject(new String(connection.getInputStream().readAllBytes()))
                        .getJSONObject("data")
                        .getJSONObject("daily_prediction")
                        .getString("prediction");
            } else {
                System.err.println("Failed to get the daily prediction. Response code: " + connection.getResponseCode());
                return "Error: " + connection.getResponseCode();
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to connect to the API. " + e.getMessage());
        }
        return null;
    }

    /**
     * Enum representing Zodiac signs and their date ranges.
     */
    public enum ZodiacSign {

        // Constants
        ARIES(MonthDay.of(3, 21), MonthDay.of(4, 20)),
        TAURUS(MonthDay.of(4, 21), MonthDay.of(5, 20)),
        GEMINI(MonthDay.of(5, 21), MonthDay.of(6, 21)),
        CANCER(MonthDay.of(6, 22), MonthDay.of(7, 22)),
        LEO(MonthDay.of(7, 23), MonthDay.of(8, 23)),
        VIRGO(MonthDay.of(8, 24), MonthDay.of(9, 23)),
        LIBRA(MonthDay.of(9, 24), MonthDay.of(10, 23)),
        SCORPIO(MonthDay.of(10, 24), MonthDay.of(11, 22)),
        SAGITTARIUS(MonthDay.of(11, 23), MonthDay.of(12, 21)),
        CAPRICORN(MonthDay.of(12, 22), MonthDay.of(1, 20)),
        AQUARIUS(MonthDay.of(1, 21), MonthDay.of(2, 19)),
        PISCES(MonthDay.of(2, 20), MonthDay.of(3, 20));

        // Attributes
        private final MonthDay startDate;
        private final MonthDay endDate;

        // Constructor
        ZodiacSign(MonthDay startDate, MonthDay endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        // Getters
        public MonthDay getStartDate() {
            return startDate;
        }

        public MonthDay getEndDate() {
            return endDate;
        }

        // Static Methods
        public static ZodiacSign getZodiacSign(String name) {
            for (ZodiacSign zodiacSign : values()) if (zodiacSign.toString().equalsIgnoreCase(name)) return zodiacSign;
            return null;
        }

        public static ZodiacSign getZodiacSign(int month, int day) {
            return getZodiacSign(MonthDay.of(month, day));
        }

        public static ZodiacSign getZodiacSign(MonthDay monthDay) {

            // Check for Zodiac Sign
            for (ZodiacSign sign : ZodiacSign.values()) {
                if (sign.getStartDate().isBefore(sign.getEndDate())) if ((monthDay.isAfter(sign.getStartDate()) || monthDay.equals(sign.getStartDate())) && (monthDay.isBefore(sign.getEndDate()) || monthDay.equals(sign.getEndDate()))) return sign;
                else if ((monthDay.isAfter(sign.getStartDate()) || monthDay.equals(sign.getStartDate())) || (monthDay.isBefore(sign.getEndDate()) || monthDay.equals(sign.getEndDate()))) return sign;
            }

            // Invalid date
            throw new IllegalArgumentException("Invalid date: " + monthDay);
        }
    }
}
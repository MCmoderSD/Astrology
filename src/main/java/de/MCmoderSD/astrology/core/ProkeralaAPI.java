package de.MCmoderSD.astrology.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.MCmoderSD.astrology.data.DailyPrediction;
import de.MCmoderSD.astrology.enums.ZodiacSign;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.MonthDay;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Provides access to the Prokerala API for fetching horoscope predictions.
 * Handles authentication and requests to the API endpoints.
 */
@SuppressWarnings("ALL")
public class ProkeralaAPI {

    // Endpoints
    private static final String TOKEN = "https://api.prokerala.com/token";
    private static final String DAILY_PREDICTION = "https://api.prokerala.com/v2/horoscope/daily";

    // Constants
    private final String clientId;
    private final String clientSecret;

    // Attributes
    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;

    // Variables
    private String accessToken;
    private Timestamp tokenExpiration;

    /**
     * Constructs a {@code ProkeralaAPI} instance with the provided client credentials.
     * Automatically authenticates and retrieves the access token.
     *
     * @param clientId     the client ID for API authentication.
     * @param clientSecret the client secret for API authentication.
     */
    public ProkeralaAPI(String clientId, String clientSecret) {

        // Set Constants
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        // Initialize Attributes
        httpClient = HttpClient.newHttpClient();
        jsonMapper = new JsonMapper();

        // Authenticate
        tokenExpiration = authenticate();
    }

    /**
     * Authenticates with the Prokerala API and retrieves an access token.
     * Automatically refreshes the token if it is expired or missing.
     *
     * @return the expiration timestamp of the new token.
     */
    private Timestamp authenticate() {

        // Check if the token is expired
        if (accessToken == null || System.currentTimeMillis() >= Objects.requireNonNull(tokenExpiration).getTime()) {
            try {

                // Construct the request body
                String requestBody = String.format(
                        "grant_type=client_credentials&client_id=%s&client_secret=%s",
                        clientId, clientSecret);

                // Build the request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(TOKEN))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                // Send the request
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) { // Success

                    // Parse the response
                    var jsonResponse = jsonMapper.readTree(response.body());
                    accessToken = jsonResponse.get("access_token").asText();
                    var expiresIn = jsonResponse.get("expires_in").asLong();

                    // Return the token expiration
                    return tokenExpiration = new Timestamp(System.currentTimeMillis() + expiresIn * 1000);

                } else throw new RuntimeException("Failed to authenticate. Response code: " + response.statusCode());

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to authenticate. " + e.getMessage());
            }
        }

        // Return the token expiration
        return tokenExpiration;
    }

    /**
     * Fetches the daily horoscope prediction for a given date.
     *
     * @param monthDay the {@link MonthDay} representing the date.
     * @return the {@link DailyPrediction} for the specified date.
     * @throws IOException          if an I/O error occurs during the request.
     * @throws InterruptedException if the request is interrupted.
     */
    public DailyPrediction dailyPrediction(MonthDay monthDay) throws IOException, InterruptedException {
        return dailyPrediction(ZodiacSign.getZodiacSign(monthDay));
    }

    /**
     * Fetches the daily horoscope prediction for a given zodiac sign.
     *
     * @param sign the {@link ZodiacSign} for which to fetch the prediction.
     * @return the {@link DailyPrediction} for the specified zodiac sign.
     * @throws IOException          if an I/O error occurs during the request.
     * @throws InterruptedException if the request is interrupted.
     */
    public DailyPrediction dailyPrediction(ZodiacSign sign) throws IOException, InterruptedException {

        // Authenticate
        tokenExpiration = authenticate();
        if (accessToken == null) throw new RuntimeException("Failed to authenticate.");

        // Construct the request
        String currentDatetime = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        String url = DAILY_PREDICTION + "?sign=" + sign.toString().toLowerCase() + "&datetime=" + currentDatetime;

        // Build the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        // Send the request
        return new DailyPrediction(sendRequest(request).get("data").get("daily_prediction"));
    }

    /**
     * Sends an HTTP request and parses the response as a {@link JsonNode}.
     *
     * @param request the {@link HttpRequest} to be sent.
     * @return the parsed {@link JsonNode} from the response body.
     * @throws IOException          if an I/O error occurs during the request.
     * @throws InterruptedException if the request is interrupted.
     */
    private JsonNode sendRequest(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("Failed to get daily prediction. Response code: " + response.statusCode());
        return jsonMapper.readTree(response.body());
    }

    /**
     * Gets the client ID used for API authentication.
     *
     * @return the client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Gets the client secret used for API authentication.
     *
     * @return the client secret.
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Gets the {@link HttpClient} used for making API requests.
     *
     * @return the {@link HttpClient} instance.
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Gets the {@link JsonMapper} used for parsing JSON responses.
     *
     * @return the {@link JsonMapper} instance.
     */
    public JsonMapper getJsonMapper() {
        return jsonMapper;
    }

    /**
     * Gets the token endpoint URL.
     *
     * @return the token endpoint URL.
     */
    public String getTokenEndpoint() {
        return TOKEN;
    }

    /**
     * Gets the daily prediction endpoint URL.
     *
     * @return the daily prediction endpoint URL.
     */
    public String getDailyPredictionEndpoint() {
        return DAILY_PREDICTION;
    }
}
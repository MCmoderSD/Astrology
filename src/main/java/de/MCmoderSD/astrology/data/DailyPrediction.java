package de.MCmoderSD.astrology.data;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringEscapeUtils;

import java.time.Instant;
import java.util.Date;

/**
 * Represents a daily horoscope prediction for a specific zodiac sign.
 * Includes details such as the sign's ID, name, prediction date, and prediction text.
 */
@SuppressWarnings("ALL")
public class DailyPrediction {

    // Attributes
    private final int signId;
    private final String signName;
    private final Date date;
    private final String prediction;


    /**
     * Constructs a {@code DailyPrediction} instance with the given details.
     *
     * @param signId     the unique identifier of the zodiac sign.
     * @param signName   the name of the zodiac sign.
     * @param date       the date of the prediction.
     * @param prediction the prediction text (HTML entities will be unescaped).
     */
    public DailyPrediction(int signId, String signName, Date date, String prediction) {
        this.signId = signId;
        this.signName = signName;
        this.date = date;
        this.prediction = StringEscapeUtils.unescapeHtml4(prediction);
    }

    /**
     * Constructs a {@code DailyPrediction} instance from a {@link JsonNode}.
     * Expects the JSON structure to include "sign_id", "sign_name", "date", and "prediction" fields.
     *
     * @param json the {@link JsonNode} containing the prediction data.
     */
    public DailyPrediction(JsonNode json) {
        signId = json.get("sign_id").asInt();
        signName = json.get("sign_name").asText();
        date = Date.from(Instant.parse(json.get("date").asText() + "T00:00:00Z"));
        prediction = StringEscapeUtils.unescapeHtml4(json.get("prediction").asText());
    }


    /**
     * Gets the unique identifier of the zodiac sign.
     *
     * @return the zodiac sign's ID.
     */
    public int getSignId() {
        return signId;
    }

    /**
     * Gets the name of the zodiac sign.
     *
     * @return the name of the zodiac sign.
     */
    public String getSignName() {
        return signName;
    }

    /**
     * Gets the date for which the prediction is made.
     *
     * @return the prediction date.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the date as a formatted string.
     *
     * @param format the date format (e.g., "yyyy-MM-dd").
     * @return the formatted date string.
     */
    public String getDate(String format) {
        return new java.text.SimpleDateFormat(format).format(date);
    }

    /**
     * Gets the prediction text for the zodiac sign.
     *
     * @return the prediction text.
     */
    public String getPrediction() {
        return prediction;
    }
}
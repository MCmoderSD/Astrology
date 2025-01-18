package de.MCmoderSD.astrology.enums;

import java.time.MonthDay;

/**
 * Enum representing the twelve zodiac signs.
 * Each zodiac sign has a start date and an end date defining its period.
 * The periods are based on the Gregorian calendar and are consistent with
 * astrological traditions.
 */
@SuppressWarnings("ALL")
public enum ZodiacSign {

    /**
     * Aries: March 21 - April 20
     */
    ARIES(MonthDay.of(3, 21), MonthDay.of(4, 20)),

    /**
     * Taurus: April 21 - May 20
     */
    TAURUS(MonthDay.of(4, 21), MonthDay.of(5, 20)),

    /**
     * Gemini: May 21 - June 21
     */
    GEMINI(MonthDay.of(5, 21), MonthDay.of(6, 21)),

    /**
     * Cancer: June 22 - July 22
     */
    CANCER(MonthDay.of(6, 22), MonthDay.of(7, 22)),

    /**
     * Leo: July 23 - August 23
     */
    LEO(MonthDay.of(7, 23), MonthDay.of(8, 23)),

    /**
     * Virgo: August 24 - September 23
     */
    VIRGO(MonthDay.of(8, 24), MonthDay.of(9, 23)),

    /**
     * Libra: September 24 - October 23
     */
    LIBRA(MonthDay.of(9, 24), MonthDay.of(10, 23)),

    /**
     * Scorpio: October 24 - November 22
     */
    SCORPIO(MonthDay.of(10, 24), MonthDay.of(11, 22)),

    /**
     * Sagittarius: November 23 - December 21
     */
    SAGITTARIUS(MonthDay.of(11, 23), MonthDay.of(12, 21)),

    /**
     * Capricorn: December 22 - January 20
     */
    CAPRICORN(MonthDay.of(12, 22), MonthDay.of(1, 20)),

    /**
     * Aquarius: January 21 - February 19
     */
    AQUARIUS(MonthDay.of(1, 21), MonthDay.of(2, 19)),

    /**
     * Pisces: February 20 - March 20
     */
    PISCES(MonthDay.of(2, 20), MonthDay.of(3, 20));

    // Attributes
    private final MonthDay startDate;
    private final MonthDay endDate;

    /**
     * Constructs a ZodiacSign with its associated start and end dates.
     *
     * @param startDate the start date of the zodiac sign period.
     * @param endDate   the end date of the zodiac sign period.
     */
    ZodiacSign(MonthDay startDate, MonthDay endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Gets the start date of the zodiac sign.
     *
     * @return the start date as a {@link MonthDay}.
     */
    public MonthDay getStartDate() {
        return startDate;
    }

    /**
     * Gets the end date of the zodiac sign.
     *
     * @return the end date as a {@link MonthDay}.
     */
    public MonthDay getEndDate() {
        return endDate;
    }

    /**
     * Retrieves a ZodiacSign based on its name.
     *
     * @param name the name of the zodiac sign (case-insensitive).
     * @return the corresponding {@link ZodiacSign}, or {@code null} if no match is found.
     */
    public static ZodiacSign getZodiacSign(String name) {
        if (name == null || name.isBlank()) return null;
        for (ZodiacSign zodiacSign : values())
            if (zodiacSign.toString().equalsIgnoreCase(name)) return zodiacSign;
        return null;
    }

    /**
     * Determines the ZodiacSign for a specific month and day.
     *
     * @param month the month (1-12).
     * @param day   the day of the month (1-31).
     * @return the corresponding {@link ZodiacSign}.
     * @throws IllegalArgumentException if the month or day is invalid.
     */
    public static ZodiacSign getZodiacSign(int month, int day) {
        if (month < 1 || month > 12 || day < 1 || day > 31)
            throw new IllegalArgumentException("Invalid date: " + month + "/" + day);
        return getZodiacSign(MonthDay.of(month, day));
    }

    /**
     * Determines the ZodiacSign for a specific {@link MonthDay}.
     *
     * @param monthDay the {@link MonthDay} to check.
     * @return the corresponding {@link ZodiacSign}.
     * @throws IllegalArgumentException if the date does not match any zodiac sign.
     */
    public static ZodiacSign getZodiacSign(MonthDay monthDay) {
        for (ZodiacSign sign : ZodiacSign.values()) {
            if (sign.getStartDate().isBefore(sign.getEndDate())) {
                if ((monthDay.isAfter(sign.getStartDate()) || monthDay.equals(sign.getStartDate())) &&
                        (monthDay.isBefore(sign.getEndDate()) || monthDay.equals(sign.getEndDate()))) {
                    return sign;
                }
            } else if ((monthDay.isAfter(sign.getStartDate()) || monthDay.equals(sign.getStartDate())) ||
                    (monthDay.isBefore(sign.getEndDate()) || monthDay.equals(sign.getEndDate()))) {
                return sign;
            }
        }
        throw new IllegalArgumentException("Invalid date: " + monthDay);
    }
}
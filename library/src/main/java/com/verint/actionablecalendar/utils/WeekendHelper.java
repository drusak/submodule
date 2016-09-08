package com.verint.actionablecalendar.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Helper class for retriving week end day according to used Locale
 *
 * Created by acheshihin on 7/27/2016.
 */
public final class WeekendHelper {

    private static final List<String> sSunWeekendDaysCountries;
    private static final List<String> sFryWeekendDaysCountries;
    private static final List<String> sFrySunWeekendDaysCountries;
    private static final List<String> sThuFryWeekendDaysCountries;
    private static final List<String> sFrySatWeekendDaysCountries;

    static {
        sSunWeekendDaysCountries = Arrays.asList("GQ", "IN", "TH", "UG");
        sFryWeekendDaysCountries = Arrays.asList("DJ", "IR");
        sFrySunWeekendDaysCountries = Collections.singletonList("BN");
        sThuFryWeekendDaysCountries = Collections.singletonList("AF");
        sFrySatWeekendDaysCountries = Arrays.asList("AE", "DZ", "BH", "BD", "EG", "IQ", "IL", "JO", "KW", "LY", "MV", "MR", "OM", "PS", "QA", "SA", "SD", "SY", "YE");
    }

    private WeekendHelper(){
        // Hidden constructor
    }


    /**
     * Returns array with week end days (in a {@link Calendar} constants form) according to
     * provided {@link Locale}
     *
     * @param locale instance of {@link Locale}
     * @return array with week end days for provided {@link Locale}
     */
    public static int[] getWeekendDays(Locale locale) {

        if (sThuFryWeekendDaysCountries.contains(locale.getCountry())) {
            return new int[]{Calendar.THURSDAY, Calendar.FRIDAY};
        }
        else if (sFrySunWeekendDaysCountries.contains(locale.getCountry())) {
            return new int[]{Calendar.FRIDAY, Calendar.SUNDAY};
        }
        else if (sFryWeekendDaysCountries.contains(locale.getCountry())) {
            return new int[]{Calendar.FRIDAY};
        }
        else if (sSunWeekendDaysCountries.contains(locale.getCountry())) {
            return new int[]{Calendar.SUNDAY};
        }
        else if (sFrySatWeekendDaysCountries.contains(locale.getCountry())) {
            return new int[]{Calendar.FRIDAY, Calendar.SATURDAY};
        }
        else {
            return new int[]{Calendar.SATURDAY, Calendar.SUNDAY};
        }
    }
}

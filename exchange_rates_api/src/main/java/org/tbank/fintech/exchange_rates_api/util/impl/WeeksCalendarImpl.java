package org.tbank.fintech.exchange_rates_api.util.impl;

import org.springframework.stereotype.Component;
import org.tbank.fintech.exchange_rates_api.util.WeeksCalendar;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

@Component
public class WeeksCalendarImpl implements WeeksCalendar {

    @Override
    public long getStartOfWeekTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToUTC(calendar.getTimeInMillis());
    }

    @Override
    public long getEndOfWeekTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return convertToUTC(calendar.getTimeInMillis());
    }

    private long convertToUTC(long unixTimeStamp) {
        ZoneId localZone = ZoneId.systemDefault();
        Instant instant = Instant.ofEpochMilli(unixTimeStamp);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, localZone);
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcDateTime.toEpochSecond();
    }
}

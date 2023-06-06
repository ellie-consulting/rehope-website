package live.rehope.site.util;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT; // RFC-3339
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    /**
     * Format millis to a RFC-3339 date.
     *
     * @param millis Epoch millis.
     * @return The date.
     */
    @NotNull
    public static String formatMillisRfc(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);

        return instant.atOffset(ZONE_OFFSET).format(DATE_TIME_FORMATTER);
    }

    /**
     * Parse an RFC-3339 string to millis.
     *
     * @param rfc Rfc string.
     * @return The epoch millis.
     */
    public static long fromRfc(String rfc) {
        return Instant.from(DATE_TIME_FORMATTER.parse(rfc)).toEpochMilli();
    }
}

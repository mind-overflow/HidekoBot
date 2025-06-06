package wtf.beatrice.hidekobot.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

public class FormatUtil
{

    private FormatUtil() {
        throw new IllegalStateException("Utility class");
    }

    // cosmetic string to print on startup.
    private static final String LOGO = """
   \s
        ██╗░░██╗██╗██████╗░███████╗██╗░░██╗░█████╗░
        ██║░░██║██║██╔══██╗██╔════╝██║░██╔╝██╔══██╗
        ███████║██║██║░░██║█████╗░░█████═╝░██║░░██║
        ██╔══██║██║██║░░██║██╔══╝░░██╔═██╗░██║░░██║
        ██║░░██║██║██████╔╝███████╗██║░╚██╗╚█████╔╝
        ╚═╝░░╚═╝╚═╝╚═════╝░╚══════╝╚═╝░░╚═╝░╚════╝░
   \s""";


    /**
     * Returns ASCII art saying the bot name.
     *
     * @return a String containing the logo
     */
    public static String getLogo()
    {
        return LOGO;
    }

    /**
     * Generate a nicely formatted time-diff String that omits unnecessary data
     * (e.g. 0 days, 0 hours, 4 minutes, 32 seconds -> 4m 32s)
     *
     * @return the formatted String
     */
    public static String getNiceTimeDiff(LocalDateTime start)
    {
        LocalDateTime now = LocalDateTime.now();
        long uptimeSeconds = ChronoUnit.SECONDS.between(start, now);

        Duration uptime = Duration.ofSeconds(uptimeSeconds);

        return getNiceDuration(uptime);
    }

    /**
     * Generate a nicely formatted duration String that omits unnecessary data
     * (e.g. 0 days, 0 hours, 4 minutes, 32 seconds -> 4m 32s)
     *
     * @return the formatted String
     */
    public static String getNiceDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
            sb.append(hours).append("h ");
            sb.append(minutes).append("m ");
        } else if (hours > 0) {
            sb.append(hours).append("h ");
            sb.append(minutes).append("m ");
        } else if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        sb.append(seconds).append("s");

        return sb.toString();
    }

    /**
     * Method to parse a string into a duration.
     * Warning: this only supports up to days; months and longer timeframes are unsupported.
     *
     * @param duration the String to parse.
     * @return a Duration of the parsed timeframe, or null if parsing failed.
     */
    @Nullable
    public static Duration parseDuration(String duration)
    {
        // sanitize a bit to avoid cluttering with garbled strings
        if(duration.length() > 16) duration = duration.substring(0, 16);
        duration = duration.replaceAll("[^\\w]", ""); //only keep digits and word characters
        duration = duration.toLowerCase();

        /* the following regex matches any number followed by any amount of characters, any amount of times.
        eg: 3d, 33hours, 32048dojg, 3d2h5m22s.
        it does not match if the [digits and characters] blocks are missing.
        eg: 33, asd, 3g5hj, 4 asd.

        {1,10} is used to limit the size of the input to parse, to avoid stack overflows.
        no one should be typing more than 10 arguments, or more than 10 digits for a single argument anyway.
         */
        if(!duration.matches("(\\d{1,10}[a-zA-Z]{1,10}){1,10}"))
            return null;

        String[] durationTimes = duration.split("[a-zA-Z]+");
        String[] durationUnits = duration.split("\\d+");

        // remove first element, because it will always be empty (there's nothing before the first character)
        durationUnits = Arrays.copyOfRange(durationUnits, 1, durationUnits.length);

        Duration fullDuration = Duration.ZERO;

        for(int i = 0; i < durationTimes.length; i++)
        {
            String durationTimeStr = durationTimes[i];
            String durationUnitStr = durationUnits[i];

            int durationValue = Integer.parseInt(durationTimeStr);
            TemporalUnit unit = parseTimeUnit(durationUnitStr);

            if(unit != null)
                fullDuration = fullDuration.plus(durationValue, unit);
            else return null; // if we failed finding the time unit, instantly quit with failed parsing.
        }

        return fullDuration;
    }

    @Nullable
    private static TemporalUnit parseTimeUnit(@NotNull String unitName)
    {
        // we won't do any sanitization, because this is a private method, and
        // we are only accessing it with things that we know for sure are already sanitized.
        unitName = unitName.toLowerCase();
        TemporalUnit timeUnit;

        /*
        parsing table
        s, se, sec, second, seconds -> SECOND
        m, mi, min, minute, minutes -> MINUTE
        h, ho, hr, hour, hours -> HOUR
        d, day, days -> DAY

        (months and longer timeframes are unsupported due to Discord restrictions)
         */

        switch (unitName)
        {
            case "s", "se", "sec", "second", "seconds" -> timeUnit = ChronoUnit.SECONDS;
            case "m", "mi", "min", "minute", "minutes" -> timeUnit = ChronoUnit.MINUTES;
            case "h", "ho", "hr", "hour", "hours" -> timeUnit = ChronoUnit.HOURS;
            case "d", "day", "days" -> timeUnit = ChronoUnit.DAYS;
            default -> timeUnit = null;
        }

        return timeUnit;

    }
}

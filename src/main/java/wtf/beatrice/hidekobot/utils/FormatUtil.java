package wtf.beatrice.hidekobot.utils;

import wtf.beatrice.hidekobot.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FormatUtil
{

    /**
     * Generate a nicely formatted uptime String that omits unnecessary data
     * (e.g. 0 days, 0 hours, 4 minutes, 32 seconds -> 4m 32s)
     *
     * @return the formatter String
     */
    public static String getNiceUptime()
    {
        LocalDateTime now = LocalDateTime.now();
        long uptimeSeconds = ChronoUnit.SECONDS.between(Configuration.getStartupTime(), now);
        Duration uptime = Duration.ofSeconds(uptimeSeconds);
        long seconds = uptime.toSecondsPart();
        long minutes = uptime.toMinutesPart();
        long hours = uptime.toHoursPart();
        long days = uptime.toDays();

        StringBuilder uptimeStringBuilder = new StringBuilder();
        if(days == 0)
        {
            if(hours == 0)
            {
                if(minutes == 0)
                {} else {
                    uptimeStringBuilder.append(minutes).append("m ");
                }
            } else {
                uptimeStringBuilder.append(hours).append("h ");
                uptimeStringBuilder.append(minutes).append("m ");
            }
        } else {
            uptimeStringBuilder.append(days).append("d ");
            uptimeStringBuilder.append(hours).append("h ");
            uptimeStringBuilder.append(minutes).append("m ");
        }
        uptimeStringBuilder.append(seconds).append("s ");

        return uptimeStringBuilder.toString();
    }
}

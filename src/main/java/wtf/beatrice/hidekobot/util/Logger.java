package wtf.beatrice.hidekobot.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Deprecated(since = "0.5.16", forRemoval = true)
public class Logger<T>
{

    // objects that we need to have for a properly formatted message
    private final String className;
    private final String format = "[%date% %time%] [%class%] %message%";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");


    // when initializing a new logger, save variables in that instance
    public Logger(Class<T> logClass)
    {
        className = logClass.getSimpleName();
    }

    /**
     * Logs a message to console, following a specific format.
     *
     * @param message the message to log
     */
    public void log(String message)
    {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = dateFormatter.format(now);
        String currentTime = timeFormatter.format(now);
        logRaw(format
                .replace("%date%", currentDate)
                .replace("%time%", currentTime)
                .replace("%class%", className)
                .replace("%message%", message));
    }

    /**
     * Logs a message to console, after delaying it.
     *
     * @param message the message to log
     * @param delay the time to wait before logging, in seconds
     */
    public void log(String message, int delay)
    {
        // create a new scheduled executor with an anonymous runnable...
        //... after waiting <delay> seconds.
        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.schedule(() -> log(message), delay, TimeUnit.SECONDS);
        }

    }

    /**
     * Prints a message to console without any formatting.
     *
     * @param message the message to log
     */
    public void logRaw(String message)
    {
        System.out.println(message);
    }


}

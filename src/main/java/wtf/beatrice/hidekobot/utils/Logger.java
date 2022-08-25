package wtf.beatrice.hidekobot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Logger
{

    // objects that we need to have for a properly formatted message
    private String className;
    private final String format = "[%date%] [%class%] %message%";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");


    // when initializing a new logger, save variables in that instance
    public Logger(Class logClass)
    {
        className = logClass.getSimpleName();
    }

    // log a message to console, with our chosen format
    public void log(String message)
    {
        LocalDateTime now = LocalDateTime.now();
        String currentTime = formatter.format(now);
        System.out.println(format
                .replace("%date%", currentTime)
                .replace("%class%", className)
                .replace("%message%", message));
    }

    // log a message to console after delaying it (in seconds).
    public void log(String message, int delay)
    {
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable()
        {
            @Override
            public void run()
            {
                log(message);
            }
        }, delay, TimeUnit.SECONDS);

    }
}

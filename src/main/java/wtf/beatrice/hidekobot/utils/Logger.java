package wtf.beatrice.hidekobot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}

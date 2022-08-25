package wtf.beatrice.hidekobot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Logger
{

    // cosmetic string to print on startup.
    private String logo =
            "██╗░░██╗██╗██████╗░███████╗██╗░░██╗░█████╗░\n" +
            "██║░░██║██║██╔══██╗██╔════╝██║░██╔╝██╔══██╗\n" +
            "███████║██║██║░░██║█████╗░░█████═╝░██║░░██║\n" +
            "██╔══██║██║██║░░██║██╔══╝░░██╔═██╗░██║░░██║\n" +
            "██║░░██║██║██████╔╝███████╗██║░╚██╗╚█████╔╝\n" +
            "╚═╝░░╚═╝╚═╝╚═════╝░╚══════╝╚═╝░░╚═╝░╚════╝░";

    // objects that we need to have for a properly formatted message
    private String className;
    private final String format = "[%date% %time%] [%class%] %message%";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");


    // when initializing a new logger, save variables in that instance
    public Logger(Class logClass)
    {
        className = logClass.getSimpleName();
    }

    // log a message to console, with our chosen format
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

    // log a message to console after delaying it (in seconds).
    public void log(String message, int delay)
    {
        // create a new scheduled executor with an anonymous runnable...
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable()
        {
            @Override
            public void run()
            {
                // log the message
                log(message);
            }
            //... after waiting X seconds.
        }, delay, TimeUnit.SECONDS);

    }

    // avoid formatting the text and print whatever is passed.
    public void logRaw(String message)
    {
        System.out.println(message);
    }

    public String getLogo()
    {
        return logo;
    }

}

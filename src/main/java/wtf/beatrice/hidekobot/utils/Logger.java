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
        Executors.newSingleThreadScheduledExecutor().schedule(() -> log(message), delay, TimeUnit.SECONDS);

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

    /**
     * Returns ASCII art saying the bot name.
     *
     * @return a String containing the logo
     */
    public String getLogo()
    {
        return logo;
    }

}

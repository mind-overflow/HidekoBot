package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.database.DatabaseManager;
import wtf.beatrice.hidekobot.listeners.MessageLogger;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Configuration
{


    private static DatabaseManager dbManager = null;
    private static boolean verbose = false;
    private static MessageLogger verbosityLogger;

    // todo: allow people to set their own user id
    private static final long botOwnerId = 979809420714332260L;

    private final static String expiryTimestampFormat = "yy/MM/dd HH:mm:ss";

    // note: discord sets interactions' expiry time to 15 minutes by default, so we can't go higher than that.
    private final static long expiryTimeSeconds = 60L;

    // used to count eg. uptime
    private static LocalDateTime startupTime;


    private static final String botVersion = "0.1.2-slash"; // we should probably find a way to make this consistent with Maven
    private static final String botName = "HidekoBot";
    private static final Color botColor = Color.PINK;

    private static final List<Command> registeredCommands = new ArrayList<>();

    private final static String defaultInviteLink =
            "https://discord.com/api/oauth2/authorize?client_id=%userid%&scope=bot+applications.commands&permissions=8";

    private static String botApplicationId = "";

    // discord api returns a broken image if you don't use specific sizes (powers of 2), so we limit it to these
    private static final int[] supportedAvatarResolutions = { 16, 32, 64, 128, 256, 512, 1024 };

    /**
     * Get an array of all the Discord-supported avatar resolutions.
     * Discord's API returns a broken image if you don't use specific sizes (powers of 2).
     *
     * @return array of supported resolutions.
     */
    public static int[] getSupportedAvatarResolutions() { return supportedAvatarResolutions; }

    /**
     * Checks if the bot has been started with the verbose argument.
     *
     * @return a boolean which is true if the bot is in verbose-mode
     */
    public static boolean isVerbose() { return verbose; }

    /**
     * Set the bot's verbosity status at runtime.
     * This also registers or unregisters the message-logger listener.
     *
     * @param v the verbosity boolean value
     */
    public static void setVerbose(boolean v)
    {
        verbose = v;

        if(v)
        {
            if(verbosityLogger == null)
            {
                verbosityLogger = new MessageLogger();
            }

            HidekoBot.getAPI().addEventListener(verbosityLogger);
        } else {
            if(verbosityLogger != null)
            {
                HidekoBot.getAPI().removeEventListener(verbosityLogger);
                verbosityLogger = null;
            }
        }
    }

    /**
     * Get the bot owner's profile id.
     *
     * @return a long of the account's id
     */
    public static long getBotOwnerId() { return botOwnerId; }

    /**
     * Set the bot's application id.
     *
     * @param id the bot's application id
     */
    public static void setBotApplicationId(String id)
    {
        botApplicationId = id;
    }

    /**
     * Get the bot's application id
     *
     * @return a string of the bot's application id
     */
    public static String getBotApplicationId() { return botApplicationId; }

    /**
     * Function to generate an invite link for the bot
     *
     * @return a string containing the invite link
     */
    public static String getInviteUrl() {
        return defaultInviteLink.replace("%userid%", botApplicationId);
    }

    /**
     * Set the already fully-initialized DatabaseManager instance, ready to be accessed and used.
     *
     * @param databaseManagerInstance the fully-initialized DatabaseManager instance.
     */
    public static void setDatabaseManagerInstance(DatabaseManager databaseManagerInstance)
    {
        dbManager = databaseManagerInstance;
    }

    /**
     * Get the fully-initialized DatabaseManager instance, ready to be used.
     *
     * @return the DatabaseManager instance.
     */
    public static @Nullable DatabaseManager getDatabaseManager() { return dbManager; }

    /**
     * Get the DateTimeFormatter string for parsing the expired messages timestamp.
     *
     * @return the String of the DateTimeFormatter format.
     */
    public static String getExpiryTimestampFormat(){ return  expiryTimestampFormat; }

    /**
     * Get the amount of seconds after which a message expires.
     *
     * @return long value of the expiry seconds.
     */
    public static long getExpiryTimeSeconds() { return expiryTimeSeconds; }


    public static String getBotName() { return botName; };

    /**
     * Get the bot's version.
     *
     * @return a String of the bot version.
     */
    public static String getBotVersion() { return botVersion; }

    /**
     * Get the bot's global color.
     *
     * @return the Color object.
     */
    public static Color getBotColor() { return botColor; }

    /**
     * Set the list of registered commands. They will be sorted alphabetically.
     *
     * @param commands a list of registered commands.
     */
    public static void setRegisteredCommands(List<Command> commands)
    {

        // sort alphabetically by field getName()
        List<Command> tempList = commands
                .stream()
                .sorted(Comparator.comparing(Command::getName))
                .toList();

        registeredCommands.addAll(tempList);
    }

    /**
     * Get a list of all bot registered commands, sorted alphabetically.
     *
     * @return a copy of the List.
     */
    public static List<Command> getRegisteredCommands()
    {
        return new ArrayList<>(registeredCommands);
    }

    /**
     * Set the bot's startup time. Generally only used at boot time.
     *
     * @param time a LocalDateTime of the startup moment.
     */
    public static void setStartupTime(LocalDateTime time)
    { startupTime = time; }


    /**
     * Get the time of when the bot was started up.
     *
     * @return a LocalDateTime object of the startup instant.
     */
    public static LocalDateTime getStartupTime() { return startupTime; }

    /**
     * Generate a nicely formatted uptime String that omits unnecessary data
     * (eg. 0 days, 0 hours, 4 minutes, 32 seconds -> 4m 32s)
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

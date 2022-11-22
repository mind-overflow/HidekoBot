package wtf.beatrice.hidekobot;

import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.datasource.ConfigurationSource;
import wtf.beatrice.hidekobot.datasource.DatabaseSource;
import wtf.beatrice.hidekobot.listeners.MessageCommandListener;
import wtf.beatrice.hidekobot.listeners.MessageLogger;
import wtf.beatrice.hidekobot.listeners.SlashCommandListener;
import wtf.beatrice.hidekobot.util.Logger;

import java.awt.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class Cache
{


    private static final Logger logger = new Logger(Cache.class);
    private static ConfigurationSource configurationSource = null;
    private static DatabaseSource databaseSource = null;
    private static boolean verbose = false;
    private static MessageLogger verbosityLogger;
    private static final long botMaintainerId = 979809420714332260L;
    private final static String expiryTimestampFormat = "yy/MM/dd HH:mm:ss";

    // note: discord sets interactions' expiry time to 15 minutes by default, so we can't go higher than that.
    private final static long expiryTimeSeconds = 15L;

    // used to count e.g. uptime
    private static LocalDateTime startupTime;

    private final static String execPath = System.getProperty("user.dir");

    private static final String botVersion = "0.3.0"; // we should probably find a way to make this consistent with Maven
    private static final String botName = "Hideko";

    private static SlashCommandListener slashCommandListener = null;
    private static MessageCommandListener messageCommandListener = null;

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
    public static long getBotOwnerId() {
        return configurationSource == null ? 0L : (Long) configurationSource.getConfigValue("bot-owner-id");
    }


    /**
     * Get the bot's token.
     *
     * @return a String of the bot's token.
     */
    public static String getBotToken() {
        return configurationSource == null ? null : (String) configurationSource.getConfigValue("bot-token");
    }

    /**
     * Get the bot maintainer's profile id.
     *
     * @return a long of the account's id
     */
    
    public static long getBotMaintainerId() { return botMaintainerId; }

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
     * Set the already fully-initialized DatabaseSource instance, ready to be accessed and used.
     *
     * @param databaseSourceInstance the fully-initialized DatabaseSource instance.
     */
    public static void setDatabaseSourceInstance(DatabaseSource databaseSourceInstance)
    {
        databaseSource = databaseSourceInstance;
    }

    /**
     * Get the fully-initialized DatabaseSource instance, ready to be used.
     *
     * @return the DatabaseSource instance.
     */
    public static @Nullable DatabaseSource getDatabaseSource() { return databaseSource; }

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
    public static Color getBotColor() {
        Color defaultColor = Color.PINK;
        if(configurationSource == null) return defaultColor;
        String colorName = (String) configurationSource.getConfigValue("bot-color");

        Color color = null;
        try {
            Field field = Color.class.getField(colorName);
            color = (Color)field.get(null);
        } catch (Exception e) {
            logger.log("Unknown color: " + colorName);
        }
        return color == null ? defaultColor : color;
    }


    //todo javadocs
    public static void setSlashCommandListener(SlashCommandListener commandListener)
    { slashCommandListener = commandListener; }

    public static SlashCommandListener getSlashCommandListener() { return slashCommandListener; }


    public static void setMessageCommandListener(MessageCommandListener commandListener)
    { messageCommandListener = commandListener; }

    public static MessageCommandListener getMessageCommandListener() { return messageCommandListener; }

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

    public static String getFullHeartBeatLink() {
        return configurationSource == null ? null : (String) configurationSource.getConfigValue("heartbeat-link");
    }
    //todo javadocs
    public static String getExecPath() { return execPath; }

    /*private static ConfigurationSource getConfigurationSource()
    { return configurationSource; }*/

    public static void setConfigurationSource(ConfigurationSource configurationSource)
    { Cache.configurationSource = configurationSource; }

}

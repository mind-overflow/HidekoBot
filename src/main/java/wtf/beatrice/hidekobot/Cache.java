package wtf.beatrice.hidekobot;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.datasources.ConfigurationEntry;
import wtf.beatrice.hidekobot.datasources.ConfigurationSource;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;
import wtf.beatrice.hidekobot.datasources.PropertiesSource;
import wtf.beatrice.hidekobot.listeners.MessageCommandListener;
import wtf.beatrice.hidekobot.listeners.MessageLogger;
import wtf.beatrice.hidekobot.listeners.SlashCommandCompletionListener;
import wtf.beatrice.hidekobot.listeners.SlashCommandListener;

import java.awt.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Cache
{

    private Cache() {
        throw new IllegalStateException("Utility class");
    }


    // todo: make this compatible with the message listener's regex
    private static final String BOT_PREFIX = "hideko";
    private static final Logger LOGGER = LoggerFactory.getLogger(Cache.class);

    // map to store results of "love calculator", to avoid people re-running the same command until
    // they get what they wanted.
    // i didn't think this was worthy of a whole database table with a runnable checking for expiration,
    // and it will get cleared after a few minutes anyway, so RAM caching is more than good enough.
    private static final HashMap<String, Integer> loveCalculatorValues = new HashMap<>();

    private static PropertiesSource propertiesSource = null;
    private static ConfigurationSource configurationSource = null;
    private static DatabaseSource databaseSource = null;
    private static boolean verbose = false;
    private static MessageLogger verbosityLogger = null;
    private static final long BOT_MAINTAINER_ID = 979809420714332260L;
    private static final String EXPIRY_TIMESTAMP_FORMAT = "yy/MM/dd HH:mm:ss";

    // note: discord sets interactions' expiry time to 15 minutes by default, so we can't go higher than that.
    private static final long EXPIRY_TIME_SECONDS = 30L;

    // used to count e.g. uptime
    private static LocalDateTime startupTime = null;

    // date of when the first bot commit was made (CEST time)
    private static final LocalDateTime botBirthDate = LocalDateTime.of(2022, 8, 25, 21, 50);

    // the scheduler that should always be used when running a scheduled task.
    private static final ScheduledExecutorService taskScheduler = Executors.newSingleThreadScheduledExecutor(); // todo: try-with-resources

    private static final String EXEC_PATH = System.getProperty("user.dir");
    private static final String BOT_NAME = "Hideko";

    private static SlashCommandListener slashCommandListener = null;
    private static SlashCommandCompletionListener slashCommandCompletionListener = null;
    private static MessageCommandListener messageCommandListener = null;

    private static final String DEFAULT_INVITE_LINK =
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
    public static synchronized boolean isVerbose() { return verbose; }

    /**
     * Set the bot's verbosity status at runtime.
     * This also registers or unregisters the message-logger listener.
     *
     * @param v the verbosity boolean value
     */
    public static synchronized void setVerbose(boolean v)
    {
        verbose = v;

        if(verbosityLogger != null)
        {
            HidekoBot.getAPI().removeEventListener(verbosityLogger);
            verbosityLogger = null;
        }

        if(v)
        {
            verbosityLogger = new MessageLogger();
            HidekoBot.getAPI().addEventListener(verbosityLogger);
        }
    }

    /**
     * Get the bot owner's profile id.
     *
     * @return a long of the account's id
     */
    public static long getBotOwnerId() {
        return configurationSource == null ? 0L : (Long) configurationSource.getConfigValue(ConfigurationEntry.BOT_OWNER_ID);
    }


    /**
     * Get the bot's token.
     *
     * @return a String of the bot's token.
     */
    public static String getBotToken() {
        return configurationSource == null ? null : (String) configurationSource.getConfigValue(ConfigurationEntry.BOT_TOKEN);
    }


    /**
     * Get the bot maintainer's profile id.
     *
     * @return a long of the account's id
     */
    
    public static long getBotMaintainerId() { return BOT_MAINTAINER_ID; }

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
        return DEFAULT_INVITE_LINK.replace("%userid%", botApplicationId);
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
     * Set the properties source instance loaded from the JAR archive.
     *
     * @param propertiesSourceInstance the properties source instance.
     */
    public static void setPropertiesSourceInstance(PropertiesSource propertiesSourceInstance)
    {
        propertiesSource = propertiesSourceInstance;
    }

    /**
     * Get the DateTimeFormatter string for parsing the expired messages timestamp.
     *
     * @return the String of the DateTimeFormatter format.
     */
    public static String getExpiryTimestampFormat(){ return EXPIRY_TIMESTAMP_FORMAT; }

    /**
     * Get the amount of seconds after which a message expires.
     *
     * @return long value of the expiry seconds.
     */
    public static long getExpiryTimeSeconds() { return EXPIRY_TIME_SECONDS; }


    public static String getBotName() { return BOT_NAME; }

    /**
     * Get the bot's version.
     *
     * @return a String of the bot version.
     */
    public static String getBotVersion() {
        return propertiesSource.getProperty("bot.version");
    }

    /**
     * Get the bot's source code URL.
     *
     * @return a String containing the base URL of the repository, including a <b>trailing slash</b>.
     */
    public static String getRepositoryUrl() {
        String url = propertiesSource.getProperty("repo.base_url");
        return url.endsWith("/") ? url : url + "/";
    }

    /**
     * Get the bot's global color.
     *
     * @return the Color object.
     */
    public static Color getBotColor() {
        Color defaultColor = Color.PINK;
        if(configurationSource == null) return defaultColor;
        String colorName = (String) configurationSource.getConfigValue(ConfigurationEntry.BOT_COLOR);

        Color color = null;
        try {
            Field field = Color.class.getField(colorName);
            color = (Color)field.get(null);
        } catch (RuntimeException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Unknown color: {}", colorName);
        }
        return color == null ? defaultColor : color;
    }


    //todo javadocs
    public static void setSlashCommandListener(SlashCommandListener commandListener)
    { slashCommandListener = commandListener; }

    public static SlashCommandListener getSlashCommandListener() { return slashCommandListener; }


    public static void setSlashCommandCompletionListener(SlashCommandCompletionListener commandCompletionListener)
    { slashCommandCompletionListener = commandCompletionListener; }

    public static SlashCommandCompletionListener getSlashCommandCompletionListener() { return slashCommandCompletionListener; }


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

    /**
     * Get the time of when the bot was created.
     *
     * @return a LocalDateTime object of the first commit's instant.
     */
    public static LocalDateTime getBotBirthDate() { return botBirthDate; }

    public static String getFullHeartBeatLink() {
        return configurationSource == null ? null : (String) configurationSource.getConfigValue(ConfigurationEntry.HEARTBEAT_LINK);
    }
    //todo javadocs
    public static String getExecPath() { return EXEC_PATH; }

    /*private static ConfigurationSource getConfigurationSource()
    { return configurationSource; }*/

    public static String getRandomOrgApiKey() {
        return configurationSource == null ? null : (String) configurationSource.getConfigValue(ConfigurationEntry.RANDOM_ORG_API_KEY);
    }

    public static void setConfigurationSource(ConfigurationSource configurationSource)
    { Cache.configurationSource = configurationSource; }

    /**
     * Get the bot's prefix
     *
     * @return a String of the bot's prefix.
     */
    public static String getBotPrefix() { return BOT_PREFIX; }

    public static void cacheLoveCalculatorValue(String userId1, String userId2, int value)
    {
        String merged = userId1 + "|" + userId2;
        loveCalculatorValues.put(merged, value);
    }

    @Nullable
    public static Integer getLoveCalculatorValue(String userId1, String userId2)
    {
        String merged1 = userId1 + "|" + userId2;
        String merged2 = userId2 + "|" + userId1;
        Integer value = null;
        value = loveCalculatorValues.get(merged1);
        if(value == null) value = loveCalculatorValues.get(merged2);
        return value;
    }

    public static void removeLoveCalculatorValue(String userId1, String userId2)
    {
        loveCalculatorValues.remove(userId1 + "|" + userId2);
        loveCalculatorValues.remove(userId2 + "|" + userId1);
    }

    public static ScheduledExecutorService getTaskScheduler() {
        return taskScheduler;
    }

}

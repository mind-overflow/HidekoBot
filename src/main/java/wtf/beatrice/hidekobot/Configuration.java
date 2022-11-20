package wtf.beatrice.hidekobot;

import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.database.DatabaseManager;
import wtf.beatrice.hidekobot.listeners.MessageLogger;

public class Configuration
{


    private static DatabaseManager dbManager = null;
    private static boolean verbose = false;
    private static MessageLogger verbosityLogger;

    // todo: allow people to set their own user id
    private static final long botOwnerId = 979809420714332260L;

    private final static String expiryTimestampFormat = "yy/MM/dd HH:mm:ss";
    private final static long expiryTimeSeconds = 60L;

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

}

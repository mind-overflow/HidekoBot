package wtf.beatrice.hidekobot;

import wtf.beatrice.hidekobot.listeners.MessageLogger;

public class Configuration
{


    private static boolean verbose = false;
    private static MessageLogger verbosityLogger;

    // todo: allow people to set their own user id
    private static final long botOwnerId = 979809420714332260L;

    private final static String defaultInviteLink =
            "https://discord.com/api/oauth2/authorize?client_id=%userid%&scope=bot+applications.commands&permissions=8";

    private static String botUserId = "";


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

    public static void seBotUserId(String id)
    {
        botUserId = id;
    }

    public static String getBotUserId() { return botUserId; }

    public static String getInviteUrl() {
        return defaultInviteLink.replace("%userid%", botUserId);
    }

}

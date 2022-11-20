package wtf.beatrice.hidekobot;

import wtf.beatrice.hidekobot.listeners.MessageLogger;

public class Configuration
{


    private static boolean verbose = false;
    private static MessageLogger verbosityLogger;
    private static boolean paused = false;
    private static String prefix = ".";

    // todo: allow people to set their own user id
    private static final long botOwnerId = 979809420714332260L;


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
     * Checks if the bot has paused all operation.
     *
     * @return a boolean which is true if the bot is currently paused
     */
    public static boolean isPaused() { return paused; }

    /**
     * Set the bot in paused or unpaused state.
     * Paused means that it will not reply to anything expect the unpause command.
     *
     * @param p a boolean specifying if the bot should be paused
     */
    public static void setPaused(boolean p) { paused = p; }

    public static long getBotOwnerId() { return botOwnerId; }

}

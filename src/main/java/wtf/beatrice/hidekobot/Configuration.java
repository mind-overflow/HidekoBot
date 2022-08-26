package wtf.beatrice.hidekobot;

public class Configuration
{


    private static boolean verbose = false;
    private static boolean paused = false;


    public static boolean isVerbose() { return verbose; }

    // WARNING: verbosity spams the logs a LOT!
    public static void setVerbose(boolean v) { verbose = v; }

    public static boolean isPaused() { return paused; }
    public static void setPaused(boolean p) { paused = p; }

}

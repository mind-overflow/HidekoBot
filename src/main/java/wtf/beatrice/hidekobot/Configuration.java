package wtf.beatrice.hidekobot;

public class Configuration
{


    private static boolean verbose = false;


    public static boolean isVerbose() { return verbose; }

    // WARNING: verbosity spams the logs a LOT!
    public static void setVerbose(boolean v) { verbose = v; }

}

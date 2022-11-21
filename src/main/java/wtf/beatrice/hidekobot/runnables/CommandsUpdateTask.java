package wtf.beatrice.hidekobot.runnables;

import net.dv8tion.jda.api.JDA;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.utils.Logger;

public class CommandsUpdateTask  implements Runnable {

    private final Logger logger;

    public CommandsUpdateTask()
    {
        logger = new Logger(getClass());
    }

    @Override
    public void run() {
        if(Cache.isVerbose()) logger.log("Refreshing commands cache...");
        JDA instance = HidekoBot.getAPI();
        if(instance == null) return;
        Cache.setRegisteredCommands(instance.retrieveCommands().complete());
        if(Cache.isVerbose()) logger.log("Commands cache refreshed!");
    }
}

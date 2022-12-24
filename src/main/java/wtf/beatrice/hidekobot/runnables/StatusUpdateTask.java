package wtf.beatrice.hidekobot.runnables;

import net.dv8tion.jda.api.entities.Activity;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.Arrays;
import java.util.List;

public class StatusUpdateTask implements Runnable
{

    List<String> statuses = Arrays.asList(
            "Hatsune Miku: Project DIVA",
            "Wii Sports",
            "Excel",
            "Mii Channel",
            "Wii Speak",
            "Minetest",
            "Mario Kart Wii"
    );

    @Override
    public void run() {
        int randomPos = RandomUtil.getRandomNumber(0, statuses.size() - 1);
        String status = statuses.get(randomPos) + " | " + Cache.getBotPrefix() + " help";
        HidekoBot.getAPI().getPresence().setActivity(Activity.playing(status));
    }
}

package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import wtf.beatrice.hidekobot.utils.Logger;

import javax.security.auth.login.LoginException;

public class HidekoBot
{
    private static Logger logger = new Logger(HidekoBot.class);

    public static void main(String[] args)
    {
        JDABuilder jdaBuilder;
        JDA jda;

        try
        {
            jdaBuilder = JDABuilder.createDefault("");
            jdaBuilder.setActivity(Activity.playing("the piano"));
            jda = jdaBuilder.build();
        } catch (LoginException e)
        {
            logger.log(e.getMessage());
            return;
        }

    }

}

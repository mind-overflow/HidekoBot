package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import wtf.beatrice.hidekobot.utils.Logger;

import javax.security.auth.login.LoginException;

public class HidekoBot
{
    private static Logger logger = new Logger(HidekoBot.class);

    public static void main(String[] args)
    {
        try
        {
            JDA jda = JDABuilder.createDefault("").build();
        } catch (LoginException e)
        {
            logger.log(e.getMessage());
        }
    }

}

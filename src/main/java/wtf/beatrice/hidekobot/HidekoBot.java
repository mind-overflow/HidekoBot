package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import wtf.beatrice.hidekobot.utils.Logger;

import javax.security.auth.login.LoginException;

public class HidekoBot
{
    private static Logger logger = new Logger(HidekoBot.class);
    private static String botToken;

    public static void main(String[] args)
    {

        // check if bot token was specified as a startup argument
        if(args.length < 1)
        {
            logger.log("Please specify your bot token!");
            return;
        }

        botToken = args[0];

        JDABuilder jdaBuilder;
        JDA jda;

        try
        {
            jdaBuilder = JDABuilder.createDefault(botToken);
            jdaBuilder.setActivity(Activity.playing("the piano"));
            jda = jdaBuilder.build();
        } catch (LoginException e)
        {
            logger.log(e.getMessage());
        }

    }

}

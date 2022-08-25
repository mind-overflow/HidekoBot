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
    private static String standardInviteLink = "https://discord.com/oauth2/authorize?client_id=%userid%&scope=bot&permissions=8";
    private static String botUserId;

    private static final String version = "0.0.1";

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
            // try to create the bot object and authenticate it with discord.
            jdaBuilder = JDABuilder.createDefault(botToken);
            jdaBuilder.setActivity(Activity.playing("the piano"));
            jda = jdaBuilder.build();
        } catch (LoginException e)
        {
            logger.log(e.getMessage()); // print the error message, omit the stack trace.
            return; // if we failed connecting and authenticating, then quit.
        }

        // find the bot's user id and generate an invite-link.
        botUserId = jda.getSelfUser().getId();
        standardInviteLink = standardInviteLink.replace("%userid%", botUserId);

        // print the bot logo.
        logger.log("Ready!\n\n" + logger.getLogo() + "\nv" + version + " - bot is ready!\n", 2);

        // log the invite-link to console so noob users can just click on it.
        logger.log("Bot User ID: " + botUserId, 5);
        logger.log("Invite Link: " + standardInviteLink, 5);



    }

}

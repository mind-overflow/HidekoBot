package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import wtf.beatrice.hidekobot.listeners.MessageListener;
import wtf.beatrice.hidekobot.listeners.MessageLogger;
import wtf.beatrice.hidekobot.utils.Logger;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class HidekoBot
{
    private static String botToken;
    private static String standardInviteLink =
            "https://discord.com/oauth2/authorize?client_id=%userid%&scope=bot&permissions=8";
    private static String botUserId;
    private static final String version = "0.0.1"; // we should probably find a way to make this consistent with Maven

    private static JDA jda;


    // create a logger instance for ease of use
    private static final Logger logger = new Logger(HidekoBot.class);

    public static void main(String[] args)
    {

        // check if bot token was specified as a startup argument
        if(args.length < 1)
        {
            logger.log("Please specify your bot token!");
            return;
        }

        // load token from args
        botToken = args[0];

        // if there are more than 1 args, then iterate through them because we have additional things to do
        if(args.length > 1) {
            List<String> argsList = new ArrayList<>();
            for(int i = 1; i < args.length; i++)
            { argsList.add(args[i]); }

            if(argsList.contains("verbose")) Configuration.setVerbose(true);
        }

        try
        {
            // try to create the bot object and authenticate it with discord.
            JDABuilder jdaBuilder = JDABuilder.createDefault(botToken);

            // enable necessary intents.
            jdaBuilder.enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.GUILD_MESSAGES
            );

            jda = jdaBuilder.build().awaitReady();
        } catch (LoginException | InterruptedException e)
        {
            logger.log(e.getMessage()); // print the error message, omit the stack trace.
            return; // if we failed connecting and authenticating, then quit.
        }

        // find the bot's user id and generate an invite-link.
        botUserId = jda.getSelfUser().getId();
        standardInviteLink = standardInviteLink.replace("%userid%", botUserId);

        // register listeners
        jda.addEventListener(new MessageListener());
        if(Configuration.isVerbose()) jda.addEventListener(new MessageLogger());

        // set the bot's status
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.playing("Hatsune Miku: Project DIVA"));

        // print the bot logo.
        logger.log("Ready!\n\n" + logger.getLogo() + "\nv" + version + " - bot is ready!\n", 2);

        // log the invite-link to console so noob users can just click on it.
        logger.log("Bot User ID: " + botUserId, 3);
        logger.log("Invite Link: " + standardInviteLink, 4);

    }
    public static JDA getAPI()
    {
        return jda;
    }

}

package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import sun.misc.Signal;
import wtf.beatrice.hidekobot.database.DatabaseManager;
import wtf.beatrice.hidekobot.listeners.ButtonInteractionListener;
import wtf.beatrice.hidekobot.listeners.MessageListener;
import wtf.beatrice.hidekobot.listeners.SlashCommandCompleter;
import wtf.beatrice.hidekobot.listeners.SlashCommandListener;
import wtf.beatrice.hidekobot.runnables.ExpiredMessageTask;
import wtf.beatrice.hidekobot.utils.Logger;
import wtf.beatrice.hidekobot.utils.SlashCommandUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HidekoBot
{
    private static String botToken;

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
        } catch (Exception e)
        {
            logger.log(e.getMessage()); // print the error message, omit the stack trace.
            shutdown(); // if we failed connecting and authenticating, then quit.
        }

        // find the bot's user/application id
        String botUserId = jda.getSelfUser().getId();
        Configuration.setBotApplicationId(botUserId);

        // store if we have to force refresh commands despite no apparent changes.
        boolean forceUpdateCommands = false;

        // if there is more than 1 arg, then iterate through them because we have additional things to do.
        // we are doing this at the end because we might need the API to be already initialized for some things.
        if(args.length > 1) {
            List<String> argsList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

            if(argsList.contains("verbose")) Configuration.setVerbose(true);
            if(argsList.contains("refresh")) forceUpdateCommands = true;
        }

        // register listeners
        jda.addEventListener(new MessageListener());
        jda.addEventListener(new SlashCommandListener());
        jda.addEventListener(new SlashCommandCompleter());
        jda.addEventListener(new ButtonInteractionListener());

        // update slash commands (delayed)
        final boolean finalForceUpdateCommands = forceUpdateCommands;
        Executors.newSingleThreadScheduledExecutor().schedule(() ->
                SlashCommandUtil.updateSlashCommands(finalForceUpdateCommands), 1, TimeUnit.SECONDS);

        // set the bot's status
        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.playing("Hatsune Miku: Project DIVA"));

        // connect to database
        logger.log("Connecting to database...");
        String dbFilePath = System.getProperty("user.dir") + File.separator + "db.sqlite"; // in current directory
        DatabaseManager dbManager = new DatabaseManager(dbFilePath);
        if(dbManager.connect() && dbManager.initDb())
        {
            logger.log("Database connection initialized!");
            Configuration.setDatabaseManagerInstance(dbManager);

            // load data here...

            logger.log("Database data loaded into memory!");
        } else {
            logger.log("Error initializing database connection!");
        }

        // start scheduled runnables
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ExpiredMessageTask task = new ExpiredMessageTask();
        int initDelay = 5;
        int periodicDelay = 5;
        scheduler.scheduleAtFixedRate(task, initDelay, periodicDelay, TimeUnit.SECONDS);

        // register shutdown interrupt signal listener for proper shutdown.
        Signal.handle(new Signal("INT"), signal -> shutdown());

        // set startup time.
        Configuration.setStartupTime(LocalDateTime.now());

        // print the bot logo.
        logger.log("\n\n" + logger.getLogo() + "\nv" + Configuration.getBotVersion() + " - bot is ready!\n", 2);


        // log the invite-link to console so noob users can just click on it.
        logger.log("Bot User ID: " + botUserId, 3);
        logger.log("Invite Link: " + Configuration.getInviteUrl(), 4);

    }
    public static JDA getAPI()
    {
        return jda;
    }

    public static void shutdown()
    {
        logger.log("WARNING! Shutting down!");
        if(jda != null) jda.shutdown();
        System.exit(0);
    }

}

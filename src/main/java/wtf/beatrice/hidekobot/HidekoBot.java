package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import sun.misc.Signal;
import wtf.beatrice.hidekobot.commands.completer.ProfileImageCommandCompleter;
import wtf.beatrice.hidekobot.commands.message.HelloCommand;
import wtf.beatrice.hidekobot.commands.slash.*;
import wtf.beatrice.hidekobot.datasources.ConfigurationSource;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;
import wtf.beatrice.hidekobot.datasources.PropertiesSource;
import wtf.beatrice.hidekobot.listeners.*;
import wtf.beatrice.hidekobot.runnables.ExpiredMessageTask;
import wtf.beatrice.hidekobot.runnables.HeartBeatTask;
import wtf.beatrice.hidekobot.runnables.RandomSeedTask;
import wtf.beatrice.hidekobot.runnables.StatusUpdateTask;
import wtf.beatrice.hidekobot.util.CommandUtil;
import wtf.beatrice.hidekobot.util.Logger;

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
    private static JDA jda;

    private static final Logger logger = new Logger(HidekoBot.class);

    public static void main(String[] args)
    {

        // load configuration
        logger.log("Loading configuration...");
        String configFilePath = Cache.getExecPath() + File.separator + "config.yml";
        ConfigurationSource configurationSource = new ConfigurationSource(configFilePath);
        configurationSource.initConfig();
        Cache.setConfigurationSource(configurationSource);
        logger.log("Configuration loaded!");

        // load properties
        logger.log("Loading properties...");
        PropertiesSource propertiesSource = new PropertiesSource();
        propertiesSource.load();
        Cache.setPropertiesSourceInstance(propertiesSource);
        logger.log("Properties loaded!");

        // check loaded bot token
        String botToken = Cache.getBotToken();
        if(botToken == null || botToken.isEmpty())
        {
            logger.log("Invalid bot token!");
            shutdown();
            return;
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
        } catch (Exception e)
        {
            logger.log(e.getMessage()); // print the error message, omit the stack trace.
            shutdown(); // if we failed connecting and authenticating, then quit.
        }

        // find the bot's user/application id
        String botUserId = jda.getSelfUser().getId();
        Cache.setBotApplicationId(botUserId);

        // store if we have to force refresh commands despite no apparent changes.
        boolean forceUpdateCommands = false;

        // if there is more than 1 arg, then iterate through them because we have additional things to do.
        // we are doing this at the end because we might need the API to be already initialized for some things.
        if(args.length > 1) {
            List<String> argsList = new ArrayList<>(Arrays.asList(args));


            // NOTE: do not replace with enhanced for, since we might need
            // to know what position we're at or do further elaboration of the string.
            // we were using this for api key parsing in the past.
            for(int i = 0; i < argsList.size(); i++)
            {
                String arg = argsList.get(i);

                if(arg.equals("verbose")) Cache.setVerbose(true);
                if(arg.equals("refresh")) forceUpdateCommands = true;
            }

        }

        // register slash commands and completers
        SlashCommandListener slashCommandListener = new SlashCommandListener();
        SlashCommandCompletionListener slashCommandCompletionListener = new SlashCommandCompletionListener();
        AvatarCommand avatarCommand = new AvatarCommand();
        ProfileImageCommandCompleter avatarCommandCompleter = new ProfileImageCommandCompleter(avatarCommand);
        slashCommandListener.registerCommand(avatarCommand);
        slashCommandCompletionListener.registerCommandCompleter(avatarCommandCompleter);
        slashCommandListener.registerCommand(new BanCommand());
        BannerCommand bannerCommand = new BannerCommand();
        ProfileImageCommandCompleter bannerCommandCompleter = new ProfileImageCommandCompleter(bannerCommand);
        slashCommandListener.registerCommand(bannerCommand);
        slashCommandCompletionListener.registerCommandCompleter(bannerCommandCompleter);
        slashCommandListener.registerCommand(new BotInfoCommand());
        slashCommandListener.registerCommand(new ClearCommand());
        slashCommandListener.registerCommand(new CoinFlipCommand());
        slashCommandListener.registerCommand(new DiceRollCommand());
        slashCommandListener.registerCommand(new DieCommand());
        slashCommandListener.registerCommand(new HelpCommand());
        slashCommandListener.registerCommand(new InviteCommand());
        slashCommandListener.registerCommand(new KickCommand());
        slashCommandListener.registerCommand(new LoveCalculatorCommand());
        slashCommandListener.registerCommand(new MagicBallCommand());
        slashCommandListener.registerCommand(new PingCommand());
        slashCommandListener.registerCommand(new SayCommand());
        slashCommandListener.registerCommand(new TimeoutCommand());
        slashCommandListener.registerCommand(new TriviaCommand());
        slashCommandListener.registerCommand(new UrbanDictionaryCommand());

        // register message commands
        MessageCommandListener messageCommandListener = new MessageCommandListener();
        messageCommandListener.registerCommand(new HelloCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.AliasCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.AvatarCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.BanCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.BannerCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.BotInfoCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.CoinFlipCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.ClearCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.DiceRollCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.HelpCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.InviteCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.KickCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.LoveCalculatorCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.MagicBallCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.SayCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.TimeoutCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.TriviaCommand());
        messageCommandListener.registerCommand(new wtf.beatrice.hidekobot.commands.message.UrbanDictionaryCommand());

        // register listeners
        Cache.setSlashCommandListener(slashCommandListener);
        Cache.setSlashCommandCompletionListener(slashCommandCompletionListener);
        Cache.setMessageCommandListener(messageCommandListener);
        jda.addEventListener(messageCommandListener);
        jda.addEventListener(slashCommandListener);
        jda.addEventListener(slashCommandCompletionListener);
        jda.addEventListener(new ButtonInteractionListener());
        jda.addEventListener(new SelectMenuInteractionListener());

        // update slash commands (delayed)
        final boolean finalForceUpdateCommands = forceUpdateCommands;
        Executors.newSingleThreadScheduledExecutor().schedule(() -> // todo: try-with-resources
                CommandUtil.updateSlashCommands(finalForceUpdateCommands), 1, TimeUnit.SECONDS);

        // set the bot's status
        jda.getPresence().setStatus(OnlineStatus.ONLINE);

        // connect to database
        logger.log("Connecting to database...");
        String dbFilePath = Cache.getExecPath() + File.separator + "db.sqlite"; // in current directory
        DatabaseSource databaseSource = new DatabaseSource(dbFilePath);
        if(databaseSource.connect() && databaseSource.initDb())
        {
            logger.log("Database connection initialized!");
            Cache.setDatabaseSourceInstance(databaseSource);

            // load data here...

            logger.log("Database data loaded into memory!");
        } else {
            logger.log("Error initializing database connection!");
        }

        // start scheduled runnables
        ScheduledExecutorService scheduler = Cache.getTaskScheduler();
        ExpiredMessageTask expiredMessageTask = new ExpiredMessageTask();
        scheduler.scheduleAtFixedRate(expiredMessageTask, 5, 5, TimeUnit.SECONDS); //every 5 seconds
        HeartBeatTask heartBeatTask = new HeartBeatTask();
        scheduler.scheduleAtFixedRate(heartBeatTask, 10, 30, TimeUnit.SECONDS); //every 30 seconds
        StatusUpdateTask statusUpdateTask = new StatusUpdateTask();
        scheduler.scheduleAtFixedRate(statusUpdateTask, 0, 60 * 5, TimeUnit.SECONDS); // every 5 minutes
        RandomSeedTask randomSeedTask = new RandomSeedTask();
        scheduler.scheduleAtFixedRate(randomSeedTask, 0, 60, TimeUnit.SECONDS); // every minute

        // register shutdown interrupt signal listener for proper shutdown.
        Signal.handle(new Signal("INT"), signal -> shutdown());

        // set startup time.
        Cache.setStartupTime(LocalDateTime.now());

        // print the bot logo.
        logger.log("\n\n" + logger.getLogo() + "\nv" + Cache.getBotVersion() + " - bot is ready!\n", 2);


        // log the invite-link to console so noob users can just click on it.
        logger.log("Bot User ID: " + botUserId, 3);
        logger.log("Invite Link: " + Cache.getInviteUrl(), 4);

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

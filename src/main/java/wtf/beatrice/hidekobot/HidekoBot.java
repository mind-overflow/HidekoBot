package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ConfigurableApplicationContext;
import wtf.beatrice.hidekobot.commands.completer.ProfileImageCommandCompleter;
import wtf.beatrice.hidekobot.commands.message.*;
import wtf.beatrice.hidekobot.commands.slash.*;
import wtf.beatrice.hidekobot.commands.slash.SlashBanCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashBannerCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashClearCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashCoinFlipCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashDiceRollCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashInviteCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashKickCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashLoveCalculatorCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashMagicBallCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashSayCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashTimeoutCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashTriviaCommand;
import wtf.beatrice.hidekobot.commands.slash.SlashUrbanDictionaryCommand;
import wtf.beatrice.hidekobot.datasources.ConfigurationSource;
import wtf.beatrice.hidekobot.datasources.PropertiesSource;
import wtf.beatrice.hidekobot.listeners.*;
import wtf.beatrice.hidekobot.runnables.ExpiredMessageTask;
import wtf.beatrice.hidekobot.runnables.HeartBeatTask;
import wtf.beatrice.hidekobot.runnables.RandomOrgSeedTask;
import wtf.beatrice.hidekobot.runnables.StatusUpdateTask;
import wtf.beatrice.hidekobot.services.CommandService;
import wtf.beatrice.hidekobot.services.DatabaseService;
import wtf.beatrice.hidekobot.util.FormatUtil;
import wtf.beatrice.hidekobot.util.RandomUtil;
import wtf.beatrice.hidekobot.util.Services;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class HidekoBot
{
    private static JDA jda;

    private static final Logger LOGGER = LoggerFactory.getLogger(HidekoBot.class);

    public static void main(String[] args)
    {

        // load configuration
        LOGGER.info("Loading configuration...");
        String configFilePath = Cache.getExecPath() + File.separator + "config.yml";
        ConfigurationSource configurationSource = new ConfigurationSource(configFilePath);
        configurationSource.initConfig();
        Cache.setConfigurationSource(configurationSource);
        LOGGER.info("Configuration loaded!");

        // load properties
        LOGGER.info("Loading properties...");
        PropertiesSource propertiesSource = new PropertiesSource();
        propertiesSource.load();
        Cache.setPropertiesSourceInstance(propertiesSource);
        LOGGER.info("Properties loaded!");

        // check loaded bot token
        String botToken = Cache.getBotToken();
        if (botToken == null || botToken.isEmpty())
        {
            LOGGER.error("Invalid bot token!");
            shutdown();
            return;
        }

        ApplicationHome home = new ApplicationHome(HidekoBot.class);
        System.setProperty("APP_HOME", home.getDir().getAbsolutePath());
        ConfigurableApplicationContext context = SpringApplication.run(HidekoBot.class, args);

        CommandService commandService = context.getBean(CommandService.class);
        DatabaseService databaseService = context.getBean(DatabaseService.class);
        Services services = new wtf.beatrice.hidekobot.util.Services(
                commandService,
                databaseService
        );
        Cache.setServices(services);

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
        } catch (InterruptedException e)
        {
            LOGGER.error(e.getMessage()); // print the error message, omit the stack trace.
            Thread.currentThread().interrupt(); // send interrupt to the thread.
            shutdown(); // if we failed connecting and authenticating, then quit.
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage()); // print the error message, omit the stack trace.
            shutdown(); // if we failed connecting and authenticating, then quit.
        }

        // find the bot's user/application id
        String botUserId = jda.getSelfUser().getId();
        Cache.setBotApplicationId(botUserId);

        // store if we have to force refresh commands despite no apparent changes.
        boolean forceUpdateCommands = false;

        // if there is at least one arg, then iterate through them because we have additional things to do.
        // we are doing this at the end because we might need the API to be already initialized for some things.
        if (args.length > 0)
        {
            List<String> argsList = new ArrayList<>(Arrays.asList(args));


            // NOTE: do not replace with enhanced for, since we might need
            // to know what position we're at or do further elaboration of the string.
            // we were using this for api key parsing in the past.
            for (int i = 0; i < argsList.size(); i++)
            {
                String arg = argsList.get(i);

                if (arg.equals("verbose")) Cache.setVerbose(true);
                if (arg.equals("refresh")) forceUpdateCommands = true;
            }

        }

        boolean enableRandomSeedUpdaterTask = false;
        // initialize random.org object if API key is provided
        {
            if (RandomUtil.isRandomOrgKeyValid())
            {
                LOGGER.info("Enabling Random.org integration... This might take a while!");
                RandomUtil.initRandomOrg();
                enableRandomSeedUpdaterTask = true;
                LOGGER.info("Random.org integration enabled!");
            }
        }

        // register slash commands and completers
        SlashCommandListener slashCommandListener = context.getBean(SlashCommandListener.class);
        SlashCommandCompletionListener slashCommandCompletionListener = context.getBean(SlashCommandCompletionListener.class);
        MessageCommandListener messageCommandListener = context.getBean(MessageCommandListener.class);
        ButtonInteractionListener buttonInteractionListener = context.getBean(ButtonInteractionListener.class);
        SelectMenuInteractionListener selectMenuInteractionListener = context.getBean(SelectMenuInteractionListener.class);
        SlashAvatarCommand slashAvatarCommand = new SlashAvatarCommand();
        ProfileImageCommandCompleter avatarCommandCompleter = new ProfileImageCommandCompleter(slashAvatarCommand);
        slashCommandListener.registerCommand(slashAvatarCommand);
        slashCommandCompletionListener.registerCommandCompleter(avatarCommandCompleter);
        slashCommandListener.registerCommand(new SlashBanCommand());
        SlashBannerCommand slashBannerCommand = new SlashBannerCommand();
        ProfileImageCommandCompleter bannerCommandCompleter = new ProfileImageCommandCompleter(slashBannerCommand);
        slashCommandListener.registerCommand(slashBannerCommand);
        slashCommandCompletionListener.registerCommandCompleter(bannerCommandCompleter);
        slashCommandListener.registerCommand(context.getBean(SlashBotInfoCommand.class));
        slashCommandListener.registerCommand(new SlashClearCommand());
        slashCommandListener.registerCommand(new SlashCoinFlipCommand());
        slashCommandListener.registerCommand(new SlashDiceRollCommand());
        slashCommandListener.registerCommand(new SlashDieCommand());
        slashCommandListener.registerCommand(new SlashHelpCommand());
        slashCommandListener.registerCommand(new SlashInviteCommand());
        slashCommandListener.registerCommand(new SlashKickCommand());
        slashCommandListener.registerCommand(new SlashLoveCalculatorCommand());
        slashCommandListener.registerCommand(new SlashMagicBallCommand());
        slashCommandListener.registerCommand(new SlashPingCommand());
        slashCommandListener.registerCommand(new SlashSayCommand());
        slashCommandListener.registerCommand(new SlashTimeoutCommand());
        slashCommandListener.registerCommand(new SlashTriviaCommand());
        slashCommandListener.registerCommand(new SlashUrbanDictionaryCommand());

        // register message commands
        messageCommandListener.registerCommand(new MessageHelloCommand());
        messageCommandListener.registerCommand(context.getBean(MessageAliasCommand.class));
        messageCommandListener.registerCommand(new MessageAvatarCommand());
        messageCommandListener.registerCommand(new MessageBanCommand());
        messageCommandListener.registerCommand(new MessageBannerCommand());
        messageCommandListener.registerCommand(context.getBean(MessageBotInfoCommand.class));
        messageCommandListener.registerCommand(new MessageCoinFlipCommand());
        messageCommandListener.registerCommand(new MessageClearCommand());
        messageCommandListener.registerCommand(new MessageDiceRollCommand());
        messageCommandListener.registerCommand(context.getBean(MessageHelpCommand.class));
        messageCommandListener.registerCommand(new MessageInviteCommand());
        messageCommandListener.registerCommand(new MessageKickCommand());
        messageCommandListener.registerCommand(new MessageLoveCalculatorCommand());
        messageCommandListener.registerCommand(new MessageMagicBallCommand());
        messageCommandListener.registerCommand(new MessageSayCommand());
        messageCommandListener.registerCommand(new MessageTimeoutCommand());
        messageCommandListener.registerCommand(new MessageTriviaCommand());
        messageCommandListener.registerCommand(new MessageUrbanDictionaryCommand());

        // register listeners
        Cache.setSlashCommandListener(slashCommandListener);
        Cache.setSlashCommandCompletionListener(slashCommandCompletionListener);
        Cache.setMessageCommandListener(messageCommandListener);
        jda.addEventListener(messageCommandListener);
        jda.addEventListener(slashCommandListener);
        jda.addEventListener(slashCommandCompletionListener);
        jda.addEventListener(buttonInteractionListener);
        jda.addEventListener(selectMenuInteractionListener);

        // update slash commands (delayed)
        final boolean finalForceUpdateCommands = forceUpdateCommands;
        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor())
        {
            executor.schedule(() -> commandService.updateSlashCommands(finalForceUpdateCommands),
                    1, TimeUnit.SECONDS);
        }

        // set the bot's status
        jda.getPresence().setStatus(OnlineStatus.ONLINE);

        // start scheduled runnables
        ScheduledExecutorService scheduler = Cache.getTaskScheduler();
        ExpiredMessageTask expiredMessageTask = new ExpiredMessageTask(services.databaseService(), services.commandService());
        scheduler.scheduleAtFixedRate(expiredMessageTask, 5L, 5L, TimeUnit.SECONDS); //every 5 seconds

        HeartBeatTask heartBeatTask = new HeartBeatTask();
        scheduler.scheduleAtFixedRate(heartBeatTask, 10L, 30L, TimeUnit.SECONDS); //every 30 seconds

        StatusUpdateTask statusUpdateTask = new StatusUpdateTask();
        scheduler.scheduleAtFixedRate(statusUpdateTask, 0L, 60L * 5L, TimeUnit.SECONDS); // every 5 minutes
        if (enableRandomSeedUpdaterTask)
        {
            RandomOrgSeedTask randomSeedTask = new RandomOrgSeedTask();
            scheduler.scheduleAtFixedRate(randomSeedTask, 15L, 15L, TimeUnit.MINUTES); // every 15 minutes
        }

        // register shutdown interrupt signal listener for proper shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(HidekoBot::preShutdown));

        // set startup time.
        Cache.setStartupTime(LocalDateTime.now());

        // print the bot logo.
        LOGGER.info("\n\n{}\nv{} - bot is ready!\n", FormatUtil.getLogo(), Cache.getBotVersion());


        // log the invite-link to console so noob users can just click on it.
        LOGGER.info("Bot User ID: {}", botUserId);
        LOGGER.info("Invite Link: {}", Cache.getInviteUrl());

    }

    public static JDA getAPI()
    {
        return jda;
    }

    public static void shutdown()
    {
        preShutdown();
        System.exit(0);
    }

    private static void preShutdown()
    {
        LOGGER.warn("WARNING! Shutting down!");
        if (jda != null) jda.shutdown();
    }

}

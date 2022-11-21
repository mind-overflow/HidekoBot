package wtf.beatrice.hidekobot.runnables;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.database.DatabaseManager;
import wtf.beatrice.hidekobot.utils.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpiredMessageTask implements Runnable {

    private final DateTimeFormatter formatter;
    private final Logger logger;
    private DatabaseManager databaseManager;


    public ExpiredMessageTask()
    {
        String format = Configuration.getExpiryTimestampFormat();
        formatter = DateTimeFormatter.ofPattern(format);
        databaseManager = Configuration.getDatabaseManager();
        logger = new Logger(getClass());
    }


    @Override
    public void run() {

        databaseManager = Configuration.getDatabaseManager();
        if(databaseManager == null) return;

        List<String> expiringMessages = Configuration.getDatabaseManager().getQueuedExpiringMessages();
        if(expiringMessages == null || expiringMessages.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for(String messageId : expiringMessages)
        {

            if(Configuration.isVerbose()) logger.log("expired check: " + messageId);

            String expiryTimestamp = databaseManager.getQueuedExpiringMessageExpiryDate(messageId);
            if(expiryTimestamp == null || expiryTimestamp.equals("")) // if missing timestamp
            {
                // count it as already expired
                databaseManager.untrackExpiredMessage(messageId);
                // move on to next message
                continue;
            }


            LocalDateTime expiryDate = LocalDateTime.parse(expiryTimestamp, formatter);
            if(now.isAfter(expiryDate))
            {
                if(Configuration.isVerbose()) logger.log("expired: " + messageId);
                disableExpired(messageId);
            }
        }



    }

    private void disableExpired(String messageId)
    {
        String guildId = databaseManager.getQueuedExpiringMessageGuild(messageId);
        String channelId = databaseManager.getQueuedExpiringMessageChannel(messageId);



        Guild guild = HidekoBot.getAPI().getGuildById(guildId);
        if(guild == null)
        {
            // if guild is not found, consider it expired
            // (server was deleted or bot was kicked)
            databaseManager.untrackExpiredMessage(messageId);
            return;
        }
        TextChannel textChannel = guild.getTextChannelById(channelId);
        if(textChannel == null)
        {
            // if channel is not found, count it as expired
            // (channel was deleted or bot permissions restricted)
            databaseManager.untrackExpiredMessage(messageId);
            return;
        }

        RestAction<Message> retrieveAction = textChannel.retrieveMessageById(messageId);


        if(Configuration.isVerbose()) logger.log("cleaning up: " + messageId);

        retrieveAction.queue(

                message -> {
                    if(message == null)
                    {
                        databaseManager.untrackExpiredMessage(messageId);
                        return;
                    }

                    List<LayoutComponent> components = message.getComponents();
                    List<LayoutComponent> newComponents = new ArrayList<>();
                    for (LayoutComponent component : components)
                    {
                        component = component.asDisabled();
                        newComponents.add(component);
                    }

                    message.editMessageComponents(newComponents).queue();
                    databaseManager.untrackExpiredMessage(messageId);
                },

                (error) -> {
                    databaseManager.untrackExpiredMessage(messageId);
                });
    }
}
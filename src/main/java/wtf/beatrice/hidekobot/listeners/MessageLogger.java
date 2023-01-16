package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageLogger extends ListenerAdapter
{
    // this class only gets loaded as a listener if verbosity is set to true on startup.

    private static final String GUILD_MESSAGE_LOG_FORMAT = "[%guild%] [#%channel%] %user%: %message%";
    private static final String DIRECT_MESSAGE_LOG_FORMAT = "[DM] %user%: %message%";

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageLogger.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        String toLog = "";
        String userName = event.getAuthor().getAsTag();
        String message = event.getMessage().getContentDisplay();

        if(event.getChannel() instanceof TextChannel)
        {
            String guildName = ((TextChannel) event.getChannel()).getGuild().getName();
            String channelName = event.getChannel().getName();

            toLog = GUILD_MESSAGE_LOG_FORMAT
                    .replace("%guild%", guildName)
                    .replace("%channel%", channelName);
        }
        else if(event.getChannel() instanceof PrivateChannel)
        {
            toLog = DIRECT_MESSAGE_LOG_FORMAT;
        }

        toLog = toLog
                .replace("%user%", userName)
                .replace("%message%", message);

        LOGGER.info(toLog);

        if(!event.getMessage().getAttachments().isEmpty())
        {
            for(Message.Attachment atch : event.getMessage().getAttachments())
            {
                LOGGER.info(atch.getUrl());
            }
        }
    }
}

package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.util.Logger;

public class MessageLogger extends ListenerAdapter
{
    // this class only gets loaded as a listener if verbosity is set to true on startup.

    private final static String guildChannelFormat = "[%guild%] [#%channel%] %user%: %message%";
    private final static String dmFormat = "[DM] %user%: %message%";

    private final Logger logger = new Logger(MessageLogger.class);

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

            toLog = guildChannelFormat
                    .replace("%guild%", guildName)
                    .replace("%channel%", channelName);
        }
        else if(event.getChannel() instanceof PrivateChannel)
        {
            toLog = dmFormat;
        }

        toLog = toLog
                .replace("%user%", userName)
                .replace("%message%", message);

        logger.log(toLog);

        if(!event.getMessage().getAttachments().isEmpty())
        {
            for(Message.Attachment atch : event.getMessage().getAttachments())
            {
                logger.log(atch.getUrl());
            }
        }
    }
}

package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.utils.Logger;

public class MessageListener extends ListenerAdapter
{

    private final Logger logger = new Logger(MessageListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        String eventMessage = event.getMessage().getContentDisplay();

        if(eventMessage.equalsIgnoreCase("hideko"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Hello there! ✨").queue();
            return;
        }

        if(eventMessage.equalsIgnoreCase("ping"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
            return;
        }

        if(eventMessage.equalsIgnoreCase("hideko verbose"))
        {
            MessageChannel channel = event.getChannel();

            boolean verbose = Configuration.isVerbose();

            String msg = verbose ? "off" : "on";
            msg = "Turning verbosity " + msg + "!";

            Configuration.setVerbose(!verbose);

            channel.sendMessage(msg).queue();
            logger.log(msg);

            return;
        }
    }
}

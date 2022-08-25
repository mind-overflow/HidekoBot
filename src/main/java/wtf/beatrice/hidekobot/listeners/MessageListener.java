package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.utils.Logger;

public class MessageListener extends ListenerAdapter
{

    private final Logger logger = new Logger(MessageListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if(event.getMessage().getContentDisplay().equalsIgnoreCase("ping"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
        }
    }

}

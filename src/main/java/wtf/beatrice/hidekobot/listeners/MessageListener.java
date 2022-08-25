package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.utils.Logger;
import wtf.beatrice.hidekobot.utils.RandomUtil;

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

        if(event.getMessage().getContentDisplay().equalsIgnoreCase("flip a coin"))
        {
            MessageChannel channel = event.getChannel();

            int rand = RandomUtil.getRandomNumber(0, 1);
            String msg;

            if(rand == 1)
            {
                msg = ":coin: It's **Heads**!";
            } else {
                msg = "It's **Tails**! :coin:";
            }

            channel.sendMessage(msg).queue();
        }
    }
}

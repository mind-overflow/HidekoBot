package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.utils.Logger;
import wtf.beatrice.hidekobot.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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


        if(event.getMessage().getContentDisplay().equalsIgnoreCase("clear the chat"))
        {
            MessageChannel channel = event.getChannel();
            int count = 10;
            int delay = 300;

            Message warn = channel.sendMessage("Clearing...").complete();

            MessageHistory.MessageRetrieveAction action = channel.getHistoryBefore(event.getMessage().getIdLong(), count);
            List<Message> messagesUnmodifiable = action.complete().getRetrievedHistory();
            List<Message> messages = new ArrayList<>(messagesUnmodifiable);
            messages.add(warn);
            messages.add(event.getMessage());
            for(Message msg : messages)
            {
                //... after waiting X seconds.
                Executors.newSingleThreadScheduledExecutor().schedule(() ->
                {
                    logger.log(msg.getContentDisplay());
                    msg.delete().complete();
                }, delay, TimeUnit.MILLISECONDS);

                delay += 500;
            }
        }
    }
}

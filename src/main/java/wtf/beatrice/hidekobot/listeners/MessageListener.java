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
        String eventMessage = event.getMessage().getContentDisplay();

        if(eventMessage.equalsIgnoreCase("ping"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong!").queue();
            return;
        }

        if(eventMessage.equalsIgnoreCase("flip a coin"))
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
            return;
        }

        if(eventMessage.toLowerCase().matches("^clear \\d+ messages$"))
        {
            MessageChannel channel = event.getChannel();

            if(!(channel instanceof GuildMessageChannel))
            {
                channel.sendMessage("Sorry! I can't delete messages here.").queue();
                return;
            }

            //only keep numbers
            eventMessage = eventMessage.replaceAll("[^\\d]", "");

            int deleteCount = Integer.parseInt(eventMessage);
            if(deleteCount < 2 || deleteCount > 98)
            {
                channel.sendMessage("I can't delete that amount of messages!").queue();
                return;
            }

            Message warn = channel.sendMessage("Clearing...").complete();

            MessageHistory.MessageRetrieveAction action = channel.getHistoryBefore(event.getMessage().getIdLong(), deleteCount);
            List<Message> messagesUnmodifiable = action.complete().getRetrievedHistory();
            List<Message> messages = new ArrayList<>(messagesUnmodifiable);
            messages.add(warn);
            messages.add(event.getMessage());

            //more than 2 messages, less than 100 for this method
            ((GuildMessageChannel) channel).deleteMessages(messages).queue();

            return;
        }
    }
}
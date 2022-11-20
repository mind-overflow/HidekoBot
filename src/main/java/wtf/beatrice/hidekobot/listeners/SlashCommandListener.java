package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandListener extends ListenerAdapter
{

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals("ping"))
        {
            event.reply("Pong!").queue();
        }

        else if (event.getName().equals("coinflip"))
        {
            int rand = RandomUtil.getRandomNumber(0, 1);
            String msg;

            if(rand == 1)
            {
                msg = ":coin: It's **Heads**!";
            } else {
                msg = "It's **Tails**! :coin:";
            }

            event.reply(msg).queue();
        }


        else if (event.getName().equals("clear"))
        {
            MessageChannel channel = event.getChannel();

            if(!(channel instanceof TextChannel))
            {
                event.reply("Sorry! I can't delete messages here.").queue();
                return;
            }

            int deleteCount = event.getOption("amount").getAsInt();
            if(deleteCount < 2 || deleteCount > 98)
            {
                event.reply("Sorry! I can't delete that amount of messages!").queue();
                return;
            }

            event.reply("Clearing...").queue();

            MessageHistory.MessageRetrieveAction action = channel.getHistoryBefore(event.getInteraction().getIdLong(), deleteCount);
            List<Message> messagesUnmodifiable = action.complete().getRetrievedHistory();
            List<Message> messages = new ArrayList<>(messagesUnmodifiable);

            //more than 2 messages, less than 100 for this method
            ((TextChannel) channel).deleteMessages(messages).queue();

        }

    }
}

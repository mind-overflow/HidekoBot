package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import wtf.beatrice.hidekobot.Cache;

import java.util.ArrayList;
import java.util.List;

public class ClearChat
{

    public static String getLabel() {
        return "clear";
    }

    public static String getDescription() {
        return "Clear the current channel's chat.";
    }

    public static Permission getPermission() {
        return Permission.MESSAGE_MANAGE;
    }

    public static String checkDMs(Channel channel)
    {
        if(!(channel instanceof TextChannel))
        { return "\uD83D\uDE22 Sorry! I can't delete messages here."; }

        return null;
    }

    public static String checkDeleteAmount(int toDeleteAmount)
    {
        if(toDeleteAmount <= 0)
        { return  "\uD83D\uDE22 Sorry, I can't delete that amount of messages!"; }

        return null;
    }

    public static int delete(int toDeleteAmount,
                             long startingMessageId,
                             MessageChannel channel)
    {
        // int to keep track of how many messages we actually deleted.
        int deleted = 0;

        int limit = 95; //discord limits this method to only 2<x<100 deletions per run.
        // we set this slightly lower to be safe, and iterate as needed.

        // increase the count by 1, because we technically aren't clearing the first ID ever
        // which is actually the slash command's ID and not a message.
        toDeleteAmount++;

        // count how many times we have to iterate this to delete the full <toDeleteAmount> messages.
        int iterations = toDeleteAmount / limit;

        //if there are some messages left, but less than <limit>, we need one more iterations.
        int remainder = toDeleteAmount % limit;
        if(remainder != 0) iterations++;

        // set the starting point.
        long messageId = startingMessageId;

        // boolean to see if we're trying to delete more messages than possible.
        boolean outOfBounds = false;

        // do iterate.
        for(int iteration = 0; iteration < iterations; iteration++)
        {
            if(outOfBounds) break;

            // set how many messages to delete for this iteration (usually <limit> unless there's a remainder)
            int iterationSize = limit;

            // if we are at the last iteration...
            if(iteration+1 == iterations)
            {
                // check if we have <limit> or fewer messages to delete
                if(remainder != 0) iterationSize = remainder;
            }

            if(iterationSize == 1)
            {
                // grab the message
                Message toDelete = channel.retrieveMessageById(messageId).complete();
                //only delete one message
                if(toDelete != null) toDelete.delete().queue();
                else outOfBounds = true;
                // increase deleted counter by 1
                deleted++;
            } else {
                // get the last <iterationSize - 1> messages.
                MessageHistory.MessageRetrieveAction action = channel.getHistoryBefore(messageId, iterationSize - 1);
                // note: first one is the most recent, last one is the oldest message.
                List<Message> messages = new ArrayList<>();
                // (we are skipping first iteration since it would return an error, given that the id is the slash command and not a message)
                if(iteration!=0) messages.add(channel.retrieveMessageById(messageId).complete());
                messages.addAll(action.complete().getRetrievedHistory());

                // check if we only have one or zero messages left (trying to delete more than possible)
                if(messages.size() <= 1)
                {
                    outOfBounds = true;
                } else {
                    // before deleting, we need to grab the <previous to the oldest> message's id for next iteration.
                    action = channel.getHistoryBefore(messages.get(messages.size() - 1).getIdLong(), 1);

                    List<Message> previousMessage = action.complete().getRetrievedHistory();

                    // if that message exists (we are not out of bounds)... store it
                    if(!previousMessage.isEmpty()) messageId = previousMessage.get(0).getIdLong();
                    else outOfBounds = true;
                }

                // queue messages for deletion
                if(messages.size() == 1)
                {
                    messages.get(0).delete().queue();
                }
                else if(!messages.isEmpty())
                {
                    try {
                        ((TextChannel) channel).deleteMessages(messages).complete();
                        /* alternatively, we could use purgeMessages, which is smarter...
                        however, it also tries to delete messages older than 2 weeks
                        which are restricted by discord, and thus has to use
                        a less efficient way that triggers rate-limiting very quickly. */

                    } catch (Exception e)
                    {

                        return -1;
                    }
                }

                // increase deleted counter by <list size>
                deleted += messages.size();
            }
        }

        return deleted;
    }

    public static Button getDismissButton()
    {
        return Button.primary("clear_dismiss", "Dismiss")
                .withEmoji(Emoji.fromUnicode("❌"));
    }

    public static String parseAmount(int deleted)
    {

        if(deleted < 1)
        {
            return "\uD83D\uDE22 Couldn't clear any message!";
        } else if(deleted == 1)
        {
            return "✂ Cleared 1 message!";
        } else {
            return "✂ Cleared " + deleted + " messages!";
        }
    }

    private void respond(Object responseFlowObj, String content)
    {
        if(responseFlowObj instanceof InteractionHook) {
            ((InteractionHook) responseFlowObj).editOriginal(content).queue();
        } else if (responseFlowObj instanceof Message) {
            ((Message) responseFlowObj).reply(content).queue();
        }
    }

    // cap the amount to avoid abuse.
    public static int getMaxAmount() { return 1000; }

}

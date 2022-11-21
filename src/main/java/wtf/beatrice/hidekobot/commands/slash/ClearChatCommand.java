package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ClearChatCommand
{

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {

        // run in a new thread, so we don't block the main one
        new Thread(() ->
        {

            MessageChannel channel = event.getChannel();

            if(!(channel instanceof TextChannel))
            {
                event.reply("\uD83D\uDE22 Sorry! I can't delete messages here.").queue();
                return;
            }

        /* get the amount from the command args.
         NULL should not be possible because we specified them as mandatory,
         but apparently the mobile app doesn't care and still sends the command if you omit the args. */
            OptionMapping amountOption = event.getOption("amount");
            int toDeleteAmount = amountOption == null ? 1 : amountOption.getAsInt();

            if(toDeleteAmount <= 0)
            {
                event.reply("\uD83D\uDE22 Sorry, I can't delete that amount of messages!").queue();
            }
            else {
                // answer by saying that the operation has begun.
                InteractionHook replyInteraction = event.reply("\uD83D\uDEA7 Clearing...").complete();

                // int to keep track of how many messages we actually deleted.
                int deleted = 0;

                int limit = 95; //discord limits this method to range 2-100. we set it to 95 to be safe.

                // increase the count by 1, because we technically aren't clearing the first ID ever
                // which is actually the slash command's ID and not a message.
                toDeleteAmount++;

                // count how many times we have to iterate this to delete the full <toDeleteAmount> messages.
                int iterations = toDeleteAmount / limit;

                //if there are some messages left, but less than <limit>, we need one more iterations.
                int remainder = toDeleteAmount % limit;
                if(remainder != 0) iterations++;

                // set the starting point.
                long messageId = event.getInteraction().getIdLong();

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
                        Message toDelete = ((TextChannel)channel).retrieveMessageById(messageId).complete();
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
                        if(iteration!=0) messages.add(((TextChannel)channel).retrieveMessageById(messageId).complete());
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
                                replyInteraction.editOriginal("\uD83D\uDE22 Sorry, I ran into an error! " + e.getMessage()).queue();
                                return; // warning: this quits everything.
                            }
                        }

                        // increase deleted counter by <list size>
                        deleted += messages.size();
                    }
                }


                Button dismissButton = Button.primary("clear_dismiss", "Dismiss")
                        .withEmoji(Emoji.fromUnicode("❌"));

                WebhookMessageEditAction<Message> webhookMessageEditAction;

                // log having deleted the messages (or not).
                if(deleted < 1)
                {
                    webhookMessageEditAction = replyInteraction.editOriginal("\uD83D\uDE22 Couldn't clear any message!");
                } else if(deleted == 1)
                {
                    webhookMessageEditAction = replyInteraction.editOriginal("✂ Cleared 1 message!");
                } else {
                    webhookMessageEditAction = replyInteraction.editOriginal("✂ Cleared " + deleted + " messages!");
                }

                Message message = webhookMessageEditAction
                        .setActionRow(dismissButton)
                        .complete();

                Configuration.getDatabaseManager().queueDisabling(message);
                Configuration.getDatabaseManager().trackRanCommandReply(message, event.getUser());

            }
        }).start();

    }

    public void dismissMessage(ButtonInteractionEvent event)
    {

        if(!(Configuration.getDatabaseManager().isUserTrackedFor(event.getUser().getId(), event.getMessageId())))
        {
            event.reply("❌ You did not run this command!").setEphemeral(true).queue();
        } else
        {
            event.getInteraction().getMessage().delete().queue();
        }
    }
}

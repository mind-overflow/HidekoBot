package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.ClearChat;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClearCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels()
    {
        return new LinkedList<>(Collections.singletonList(ClearChat.getLabel()));
    }

    @Override
    public List<Permission> getPermissions()
    {
        return Collections.singletonList(ClearChat.getPermission());
    }

    @Override
    public boolean passRawArgs()
    {
        return false;
    }

    @NotNull
    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.MODERATION;
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Clear the current channel's chat history.";
    }

    @Nullable
    @Override
    public String getUsage()
    {
        return "[amount]";
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        // check if user is trying to run command in dms.
        String error = ClearChat.checkDMs(event.getChannel());
        if (error != null)
        {
            event.getMessage().reply(error).queue();
            return;
        }

        // get the amount from the command args.
        Integer toDeleteAmount;
        if (args.length == 0) toDeleteAmount = 1;
        else
        {
            try
            {
                toDeleteAmount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e)
            {
                toDeleteAmount = 0;
            }
        }

        // cap the amount to avoid abuse.
        if (toDeleteAmount > ClearChat.getMaxAmount()) toDeleteAmount = 0;

        error = ClearChat.checkDeleteAmount(toDeleteAmount);
        if (error != null)
        {
            event.getMessage().reply(error).queue();
            return;
        }

        // answer by saying that the operation has begun.
        String content = "\uD83D\uDEA7 Clearing...";
        Message botMessage = event.getMessage().reply(content).complete();

        int deleted = ClearChat.delete(toDeleteAmount,
                event.getMessageIdLong(),
                event.getChannel());

        // get a nicely formatted message that logs the deletion of messages.
        content = ClearChat.parseAmount(deleted);

        // edit the message text and attach a button.
        Button dismiss = ClearChat.getDismissButton();
        Message finalMessage = event.getChannel().sendMessage(content).setActionRow(dismiss).complete();

        // add the message to database.
        Cache.getDatabaseSource().queueDisabling(finalMessage);
        Cache.getDatabaseSource().trackRanCommandReply(finalMessage, event.getAuthor());

        // delete the sender's message.
        event.getMessage().delete().queue();
        // delete the "clearing" info message.
        botMessage.delete().queue();

    }

}

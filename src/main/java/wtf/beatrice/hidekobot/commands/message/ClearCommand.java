package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.ClearChat;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ClearCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList(ClearChat.getLabel()));
    }

    @Override
    public List<Permission> getPermissions() { return Collections.singletonList(ClearChat.getPermission()); }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        // start a new thread, because we are doing synchronous, thread-blocking operations!
        new Thread(() ->
        {
            String senderId = event.getMessage().getAuthor().getId();

            // check if user is trying to run command in dms.
            String error = ClearChat.checkDMs(event.getChannel());
            if (error != null) {
                event.getMessage().reply(error).queue();
                return;
            }

            // get the amount from the command args.
            Integer toDeleteAmount;
            if (args.length == 0) toDeleteAmount = 1;
            else toDeleteAmount = Integer.parseInt(args[0]);

            error = ClearChat.checkDeleteAmount(toDeleteAmount);
            if (error != null) {
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
            // ^ todo: maybe the dismiss button should also delete the original message sent by the user?
            // todo: but then, we need to differentiate between command type in the database, and store
            // todo: that message's id too.
            botMessage = botMessage.editMessage(content).setActionRow(dismiss).complete();

            // add the message to database.
            Cache.getDatabaseSource().queueDisabling(botMessage);
            Cache.getDatabaseSource().trackRanCommandReply(botMessage, event.getAuthor());
        }).start();

    }

}

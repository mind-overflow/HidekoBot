package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.commands.base.Say;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SayCommand implements MessageCommand
{


    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("say"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() { return Collections.singletonList(Say.getPermission()); }

    @Override
    public boolean passRawArgs() {
        return true;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {

        String messageContent;
        if(args.length != 0 && !args[0].isEmpty())
        {
            messageContent = args[0];
        } else {
            event.getMessage().reply("\uD83D\uDE20 Hey, you have to tell me what to say!")
                    .queue();
            return;
        }

        event.getChannel().sendMessage(messageContent).queue();
        event.getMessage().delete().queue();

    }
}

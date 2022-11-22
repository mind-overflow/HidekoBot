package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HelloCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Arrays.asList("hi", "hello", "heya"));
    }

    @Override
    public List<Permission> getPermissions() { return null; }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        String senderId = event.getMessage().getAuthor().getId();
        event.getMessage().reply("Hi, <@" + senderId + ">! :sparkles:").queue();
    }

}

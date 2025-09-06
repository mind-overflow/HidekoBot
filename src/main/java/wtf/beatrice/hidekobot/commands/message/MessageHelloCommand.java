package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class MessageHelloCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels()
    {
        return new LinkedList<>(Arrays.asList("hi", "hello", "heya"));
    }

    @Override
    public List<Permission> getPermissions()
    {
        return null;
    }

    @Override
    public boolean passRawArgs()
    {
        return false;
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Get pinged by the bot.";
    }

    @Nullable
    @Override
    public String getUsage()
    {
        return null;
    }

    @NotNull
    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.FUN;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        String sender = event.getMessage().getAuthor().getAsMention();
        event.getMessage().reply("Hi, " + sender + "! :sparkles:").queue();
    }

}

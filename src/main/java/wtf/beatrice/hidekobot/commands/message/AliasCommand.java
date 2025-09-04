package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.Alias;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AliasCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels()
    {
        return new LinkedList<>(Arrays.asList("alias", "aliases"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions()
    {
        return null; // anyone can use it
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
        return CommandCategory.TOOLS;
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "See other command aliases.";
    }

    @Nullable
    @Override
    public String getUsage()
    {
        return "<command>";
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        if (args.length == 0)
        {
            event.getMessage().reply("\uD83D\uDE20 Hey, you have to specify a command!").queue();
            return;
        }

        String commandLabel = args[0].toLowerCase();
        MessageCommand command = Cache.getMessageCommandListener().getRegisteredCommand(commandLabel);
        if (command == null)
        {
            event.getMessage().reply("Unrecognized command: `" + commandLabel + "`!").queue(); // todo prettier
            return;
        }

        String aliases = Alias.generateNiceAliases(command);
        aliases = "Aliases for **" + command.getCommandLabels().get(0) + "**: " + aliases;

        event.getMessage()
                .reply(aliases)
                .queue();

    }
}

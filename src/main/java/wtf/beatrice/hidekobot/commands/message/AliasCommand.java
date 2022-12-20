package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AliasCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Arrays.asList("alias", "aliases"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null; // anyone can use it
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TOOLS;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        if(args.length == 0)
        {
            event.getMessage().reply("\uD83D\uDE20 Hey, you have to specify a command!").queue();
            return;
        }

        String commandLabel = args[0].toLowerCase();
        MessageCommand command = Cache.getMessageCommandListener().getRegisteredCommand(commandLabel);
        if(command == null)
        {
            event.getMessage().reply("Unrecognized command: `" + commandLabel + "`!").queue(); // todo prettier
            return;
        }

        LinkedList<String> aliases = command.getCommandLabels();
        StringBuilder aliasesStringBuilder = new StringBuilder();
        aliasesStringBuilder.append("Aliases for **").append(aliases.get(0)).append("**: ");
        for(int i = 0; i < aliases.size(); i++)
        {
            aliasesStringBuilder.append("`").append(aliases.get(i)).append("`");

            if(i + 1 != aliases.size())
                aliasesStringBuilder.append(", "); // separate with comma except on last iteration
        }

        event.getMessage()
                .reply(aliasesStringBuilder.toString())
                .queue();

    }
}

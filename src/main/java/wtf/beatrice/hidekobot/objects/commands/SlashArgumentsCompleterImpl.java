package wtf.beatrice.hidekobot.objects.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class SlashArgumentsCompleterImpl implements SlashArgumentsCompleter
{
    private final SlashCommand parentCommand;

    public SlashArgumentsCompleterImpl(SlashCommand parentCommand)
    {
        this.parentCommand = parentCommand;
    }

    public SlashCommand getCommand()
    {
        return parentCommand;
    }

    public void runCompletion(@NotNull CommandAutoCompleteInteractionEvent event)
    {
        return;
    }
}

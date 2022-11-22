package wtf.beatrice.hidekobot.objects.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface SlashArgumentsCompleter
{

    /**
     * Get the parent slash command's object.
     *
     * @return the command object.
     */
    SlashCommand getCommand();
    /**
     * Run the argument-completion logic by parsing the event and replying accordingly.
     *
     * @param event the received auto-complete event.
     */
    void runCompletion(@NotNull CommandAutoCompleteInteractionEvent event);
}

package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface SlashCommand
{
    /**
     * Get the command's registered label, or how Discord sees and runs the registered command.
     *
     * @return the command label.
     */
    String getCommandName();

    /**
     * Run the command logic by parsing the event and replying accordingly.
     *
     * @param event the received slash command event.
     */
    void runSlashCommand(@NotNull SlashCommandInteractionEvent event);

}

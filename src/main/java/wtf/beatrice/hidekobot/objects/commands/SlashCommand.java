package wtf.beatrice.hidekobot.objects.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
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
     * Get a JDA command data object that will then be used to tell the Discord API the specifics of this
     * command.
     *
     * @return the command data object.
     */
    CommandData getSlashCommandData();
    /**
     * Run the command logic by parsing the event and replying accordingly.
     *
     * @param event the received slash command event.
     */
    void runSlashCommand(@NotNull SlashCommandInteractionEvent event);

}

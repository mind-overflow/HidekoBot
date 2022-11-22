package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface SlashCommand
{
    String getCommandName();
    void runSlashCommand(@NotNull SlashCommandInteractionEvent event);

}

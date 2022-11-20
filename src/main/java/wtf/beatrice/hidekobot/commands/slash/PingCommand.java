package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class PingCommand
{
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.reply("Pong!").queue();
    }
}

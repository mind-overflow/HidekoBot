package wtf.beatrice.hidekobot.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class PingCommand
{
    public PingCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.reply("Pong!").queue();
    }
}

package wtf.beatrice.hidekobot.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;

public class InviteCommand
{
    public InviteCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.reply("Here's your link ✨ " + Configuration.getInviteUrl()).setEphemeral(true).queue();
    }
}

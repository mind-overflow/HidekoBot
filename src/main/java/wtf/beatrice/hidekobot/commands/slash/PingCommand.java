package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.objects.SlashCommand;

public class PingCommand implements SlashCommand
{
    @Override
    public String getCommandName() {
        return "ping";
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.reply("Pong!").queue();
    }
}

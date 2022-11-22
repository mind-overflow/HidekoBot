package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class PingCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash("ping",
                "Test if the bot is responsive.");
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.reply("Pong!").queue();
    }
}

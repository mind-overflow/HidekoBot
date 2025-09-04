package wtf.beatrice.hidekobot.objects.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class SlashCommandImpl implements SlashCommand
{

    @Override
    public String getCommandName()
    {
        return getSlashCommandData().getName();
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return null;
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.reply("Base command implementation").queue();
    }
}

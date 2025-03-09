package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DieCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData() {
        return Commands.slash("die", "Stop the bot's process.")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        if(Cache.getBotOwnerId() != event.getUser().getIdLong())
        {
            event.reply("Sorry, only the bot owner can run this command!").setEphemeral(true).queue();
        } else {
            event.reply("Going to sleep! Cya ✨").queue();
            try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
                executor.schedule(HidekoBot::shutdown, 3, TimeUnit.SECONDS);
            }
        }
    }
}

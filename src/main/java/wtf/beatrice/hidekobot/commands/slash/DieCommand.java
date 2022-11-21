package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DieCommand
{

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        if(Cache.getBotOwnerId() != event.getUser().getIdLong())
        {
            event.reply("Sorry, only the bot owner can run this command!").setEphemeral(true).queue();
        } else {
            event.reply("Going to sleep! Cya ✨").queue();
            Executors.newSingleThreadScheduledExecutor().schedule(HidekoBot::shutdown, 3, TimeUnit.SECONDS);
        }
    }
}

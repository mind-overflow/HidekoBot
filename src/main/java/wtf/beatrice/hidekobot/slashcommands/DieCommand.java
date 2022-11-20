package wtf.beatrice.hidekobot.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.HidekoBot;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DieCommand
{

    public DieCommand(@NotNull SlashCommandInteractionEvent event)
    {
        if(Configuration.getBotOwnerId() != event.getMember().getIdLong())
        {
            event.reply("Sorry, only the bot owner can run this command!").setEphemeral(true).queue();
        } else {
            event.reply("Going to sleep! Cya :sparkles:").queue();
            Executors.newSingleThreadScheduledExecutor().schedule(HidekoBot::shutdown, 3, TimeUnit.SECONDS);
        }
    }
}
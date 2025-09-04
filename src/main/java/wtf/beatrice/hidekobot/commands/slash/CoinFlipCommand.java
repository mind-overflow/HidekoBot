package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class CoinFlipCommand extends SlashCommandImpl
{

    @Override
    public CommandData getSlashCommandData()
    {
        return Commands.slash("coinflip",
                "Flip a coin and get head or tails.");
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // perform coin flip
        event.reply(CoinFlip.genRandom())
                .addActionRow(CoinFlip.getReflipButton())
                .queue((interaction) ->
                {
                    // set the command as expiring and restrict it to the user who ran it
                    interaction.retrieveOriginal().queue((message) ->
                    {
                        CoinFlip.trackAndRestrict(message, event.getUser());
                    }, (error) -> {
                    });
                }, (error) -> {
                });
    }


}

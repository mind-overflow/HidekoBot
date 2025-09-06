package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

@Component
public class SlashCoinFlipCommand extends SlashCommandImpl
{
    private final CoinFlip coinFlip;

    public SlashCoinFlipCommand(@Autowired CoinFlip coinFlip)
    {
        this.coinFlip = coinFlip;
    }

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
        event.reply(coinFlip.genRandom())
                .addActionRow(coinFlip.getReflipButton())
                .queue((interaction) ->
                {
                    // set the command as expiring and restrict it to the user who ran it
                    interaction.retrieveOriginal().queue((message) ->
                    {
                        coinFlip.trackAndRestrict(message, event.getUser());
                    }, (error) -> {
                    });
                }, (error) -> {
                });
    }


}

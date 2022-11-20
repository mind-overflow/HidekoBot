package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.utils.RandomUtil;

public class CoinFlipCommand
{

    public CoinFlipCommand(@NotNull SlashCommandInteractionEvent event)
    {
        int rand = RandomUtil.getRandomNumber(0, 1);
        String msg;

        if(rand == 1)
        {
            msg = ":coin: It's **Heads**!";
        } else {
            msg = "It's **Tails**! :coin:";
        }

        event.reply(msg).queue();
    }
}

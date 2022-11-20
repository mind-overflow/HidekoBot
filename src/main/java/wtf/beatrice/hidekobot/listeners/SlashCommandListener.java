package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.utils.RandomUtil;

public class SlashCommandListener extends ListenerAdapter
{

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals("ping"))
        {
            event.reply("Pong!").queue();
        } else if (event.getName().equals("coinflip"))
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
}

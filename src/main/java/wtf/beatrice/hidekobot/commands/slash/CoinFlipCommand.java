package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.utils.RandomUtil;

import java.util.List;

public class CoinFlipCommand
{

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        int rand = RandomUtil.getRandomNumber(0, 1);
        String msg;

        if(rand == 1)
        {
            msg = ":coin: It's **Heads**!";
        } else {
            msg = "It's **Tails**! :coin:";
        }

        event.reply(msg)
                .addActionRow(Button.primary("reflip", "Flip again")
                        .withEmoji(Emoji.fromFormatted("\uD83E\uDE99")) // coin emoji
                ).queue();
    }

    public void buttonReflip(ButtonInteractionEvent event)
    {
        List<ActionRow> actionRows = event.getMessage().getActionRows();
        actionRows.set(0, actionRows.get(0).asDisabled());
        event.editComponents(actionRows).queue();

        int rand = RandomUtil.getRandomNumber(0, 1);
        String msg;

        if(rand == 1)
        {
            msg = ":coin: It's **Heads**!";
        } else {
            msg = "It's **Tails**! :coin:";
        }

        event.getHook().sendMessage(msg)
                .addActionRow(Button.primary("reflip", "Flip again")
                        .withEmoji(Emoji.fromFormatted("\uD83E\uDE99")) // coin emoji
                ).queue();
    }

}

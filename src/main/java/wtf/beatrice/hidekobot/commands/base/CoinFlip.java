package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class CoinFlip
{

    private CoinFlip()
    {
        throw new IllegalStateException("Utility class");
    }

    public static Button getReflipButton()
    {
        return Button.primary("coinflip_reflip", "Flip again")
                .withEmoji(Emoji.fromUnicode("\uD83E\uDE99"));
    }

    public static String genRandom()
    {
        int rand = RandomUtil.getRandomNumber(0, 1);
        String msg;

        if (rand == 1)
        {
            msg = ":coin: It's **Heads**!";
        } else
        {
            msg = "It's **Tails**! :coin:";
        }

        return msg;
    }

    public static void buttonReFlip(ButtonInteractionEvent event)
    {
        // Ack ASAP to avoid 3s timeout
        event.deferEdit().queue(hook -> {
            // Permission check **after** ack
            if (!Cache.getServices().databaseService().isUserTrackedFor(event.getUser().getId(), event.getMessageId()))
            {
                hook.sendMessage("❌ You did not run this command!").setEphemeral(true).queue();
                return;
            }

            // Disable all components on the original message
            List<ActionRow> oldRows = event.getMessage().getActionRows();
            List<ActionRow> disabledRows = new ArrayList<>(oldRows.size());
            for (ActionRow row : oldRows)
            {
                disabledRows.add(row.asDisabled());
            }
            hook.editOriginalComponents(disabledRows).queue();

            // Send a follow-up with a fresh button
            hook.sendMessage(genRandom())
                    .addActionRow(getReflipButton())
                    .queue(msg -> trackAndRestrict(msg, event.getUser()), err -> {
                    });
        }, failure -> {
            // Rare: if we couldn't ack, try best-effort fallbacks
            try
            {
                List<ActionRow> oldRows = event.getMessage().getActionRows();
                List<ActionRow> disabledRows = new ArrayList<>(oldRows.size());
                for (ActionRow row : oldRows) disabledRows.add(row.asDisabled());
                event.getMessage().editMessageComponents(disabledRows).queue();
            } catch (Exception ignored)
            {
            }

            event.getChannel().sendMessage(genRandom())
                    .addActionRow(getReflipButton())
                    .queue(msg -> trackAndRestrict(msg, event.getUser()), err -> {
                    });
        });
    }

    public static void trackAndRestrict(Message replyMessage, User user)
    {
        Cache.getServices().databaseService().queueDisabling(replyMessage);
        Cache.getServices().databaseService().trackRanCommandReply(replyMessage, user);
    }
}

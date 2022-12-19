package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.List;

public class CoinFlip
{


    public static Button getReflipButton() {
        return Button.primary("coinflip_reflip", "Flip again")
                .withEmoji(Emoji.fromFormatted("\uD83E\uDE99"));
    }

    public static String genRandom()
    {
        int rand = RandomUtil.getRandomNumber(0, 1);
        String msg;

        if(rand == 1)
        {
            msg = ":coin: It's **Heads**!";
        } else {
            msg = "It's **Tails**! :coin:";
        }

        return msg;
    }

    public static void buttonReFlip(ButtonInteractionEvent event)
    {
        // check if the user interacting is the same one who ran the command
        if(!(Cache.getDatabaseSource().isUserTrackedFor(event.getUser().getId(), event.getMessageId())))
        {
            event.reply("❌ You did not run this command!").setEphemeral(true).queue();
            return;
        }

        // set old message's button as disabled
        List<ActionRow> actionRows = event.getMessage().getActionRows();
        actionRows.set(0, actionRows.get(0).asDisabled());
        event.editComponents(actionRows).queue();

        // perform coin flip
        event.getHook().sendMessage(genRandom())
                .addActionRow(getReflipButton())
                .queue((message) ->
                {
                    // set the command as expiring and restrict it to the user who ran it
                    trackAndRestrict(message, event.getUser());
                }, (error) -> {});
    }

    public static void trackAndRestrict(Message replyMessage, User user)
    {
        Cache.getDatabaseSource().queueDisabling(replyMessage);
        Cache.getDatabaseSource().trackRanCommandReply(replyMessage, user);
    }
}

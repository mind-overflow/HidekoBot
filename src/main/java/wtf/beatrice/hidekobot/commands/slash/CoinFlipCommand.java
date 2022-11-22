package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.List;

public class CoinFlipCommand extends SlashCommandImpl
{

    @Override
    public CommandData getSlashCommandData()
    {
        return Commands.slash("coinflip",
                "Flip a coin and get head or tails.");
    }

    private final Button reflipButton = Button.primary("coinflip_reflip", "Flip again")
            .withEmoji(Emoji.fromFormatted("\uD83E\uDE99"));

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // perform coin flip
        event.reply(genRandom())
                .addActionRow(reflipButton)
                .queue((interaction) ->
                {
                    // set the command as expiring and restrict it to the user who ran it
                    interaction.retrieveOriginal().queue((message) ->
                    {
                        trackAndRestrict(message, event.getUser());
                    }, (error) -> {});
                }, (error) -> {});

    }

    public void buttonReFlip(ButtonInteractionEvent event)
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
                .addActionRow(reflipButton)
                .queue((message) ->
                {
                    // set the command as expiring and restrict it to the user who ran it
                    trackAndRestrict(message, event.getUser());
                }, (error) -> {});
    }

    private void trackAndRestrict(Message replyMessage, User user)
    {
        String replyMessageId = replyMessage.getId();

        Cache.getDatabaseSource().queueDisabling(replyMessage);
        Cache.getDatabaseSource().trackRanCommandReply(replyMessage, user);
    }

    private String genRandom()
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

}

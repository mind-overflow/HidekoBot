package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.ClearChat;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class ClearCommand extends SlashCommandImpl
{

    @Override
    public CommandData getSlashCommandData() {
        return Commands.slash(ClearChat.getLabel(),
                        ClearChat.getDescription())
                .addOption(OptionType.INTEGER, "amount", "The amount of messages to delete.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(ClearChat.getPermission()));
    }

    @Override

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        // check if user is trying to run command in dms.
        String error = ClearChat.checkDMs(event.getChannel());
        if(error != null)
        {
            event.getHook().editOriginal(error).queue();
            return;
        }

    /* get the amount from the command args.
     NULL should not be possible because we specified them as mandatory,
     but apparently the mobile app doesn't care and still sends the command if you omit the args. */
        OptionMapping amountOption = event.getOption("amount");
        int toDeleteAmount = amountOption == null ? 1 : amountOption.getAsInt();

        // cap the amount to avoid abuse.
        if(toDeleteAmount > ClearChat.getMaxAmount()) toDeleteAmount = 0;

        error = ClearChat.checkDeleteAmount(toDeleteAmount);
        if(error != null)
        {
            event.getHook().editOriginal(error).queue();
            return;
        }

        // answer by saying that the operation has begun.
        String content = "\uD83D\uDEA7 Clearing...";
        Message botMessage = event.getHook().editOriginal(content).complete();

        // actually delete the messages.
        int deleted = ClearChat.delete(toDeleteAmount,
                event.getInteraction().getIdLong(),
                event.getChannel());

        // get a nicely formatted message that logs the deletion of messages.
        content = ClearChat.parseAmount(deleted);

        // edit the message text and attach a button.
        Button dismiss = ClearChat.getDismissButton();
        botMessage = botMessage.editMessage(content).setActionRow(dismiss).complete();

        // add the message to database.
        Cache.getDatabaseSource().queueDisabling(botMessage);
        Cache.getDatabaseSource().trackRanCommandReply(botMessage, event.getUser());

    }
}

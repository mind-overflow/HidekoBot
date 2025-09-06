package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.commands.base.Invite;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

@Component
public class SlashInviteCommand extends SlashCommandImpl
{
    private final Invite invite;

    public SlashInviteCommand(@NotNull Invite invite)
    {
        this.invite = invite;
    }

    @Override
    public CommandData getSlashCommandData()
    {
        return Commands.slash("invite", "Get an invite link for the bot.");
    }


    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because this might take a moment
        ReplyCallbackAction replyCallbackAction = event.deferReply();

        // only make message permanent in DMs
        if (event.getChannelType() != ChannelType.PRIVATE)
        {
            replyCallbackAction = replyCallbackAction.setEphemeral(true);
        }
        replyCallbackAction.queue();

        MessageEmbed inviteEmbed = invite.generateEmbed();
        Button inviteButton = invite.getInviteButton();

        WebhookMessageEditAction<Message> reply =
                event.getHook()
                        .editOriginalEmbeds(inviteEmbed)
                        .setActionRow(inviteButton);

        reply.queue();
    }
}

package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.HidekoBot;

public class InviteCommand
{
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because this might take a moment
        ReplyCallbackAction replyCallbackAction = event.deferReply();
        // only make message permanent in DMs
        if(event.getChannelType() != ChannelType.PRIVATE)
        {
            replyCallbackAction = replyCallbackAction.setEphemeral(true);
        }
        replyCallbackAction.queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        //embed processing
        {
            embedBuilder.setColor(Configuration.getBotColor());
            String avatarUrl = HidekoBot.getAPI().getSelfUser().getAvatarUrl();
            if(avatarUrl != null) embedBuilder.setThumbnail(avatarUrl);
            embedBuilder.setTitle("Invite");
            embedBuilder.appendDescription("Click on the button below to invite " +
                    Configuration.getBotName() +
                    " to your server!");
        }

        String inviteUrl = Configuration.getInviteUrl();
        Button inviteButton = Button.link(inviteUrl, "Invite " + Configuration.getBotName())
                .withEmoji(Emoji.fromUnicode("\uD83C\uDF1F"));

        WebhookMessageEditAction<Message> reply =
                event.getHook()
                .editOriginalEmbeds(embedBuilder.build())
                .setActionRow(inviteButton);

        reply.queue();
    }
}

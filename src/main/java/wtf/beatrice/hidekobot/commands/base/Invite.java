package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;

public class Invite
{

    private Invite()
    {
        throw new IllegalStateException("Utility class");
    }

    public static MessageEmbed generateEmbed()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        //embed processing
        {
            embedBuilder.setColor(Cache.getBotColor());
            String avatarUrl = HidekoBot.getAPI().getSelfUser().getAvatarUrl();
            if (avatarUrl != null) embedBuilder.setThumbnail(avatarUrl);
            embedBuilder.setTitle("Invite");
            embedBuilder.appendDescription("Click on the button below to invite " +
                    Cache.getBotName() +
                    " to your server!");
        }

        return embedBuilder.build();
    }

    public static Button getInviteButton()
    {
        String inviteUrl = Cache.getInviteUrl();
        return Button.link(inviteUrl, "Invite " + Cache.getBotName())
                .withEmoji(Emoji.fromUnicode("\uD83C\uDF1F"));
    }
}

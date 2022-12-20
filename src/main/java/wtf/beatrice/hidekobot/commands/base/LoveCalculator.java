package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.RandomUtil;

public class LoveCalculator
{
    public static MessageEmbed buildEmbed(User author, User user1, User user2)
    {
        int loveAmount = RandomUtil.getRandomNumber(0, 100);

        String formattedAmount = loveAmount + "%";
        if(loveAmount <= 30) formattedAmount += "... \uD83D\uDE22";
        else if(loveAmount < 60) formattedAmount += "! \uD83E\uDDD0";
        else if(loveAmount < 75) formattedAmount += "!!! \uD83E\uDD73";
        else formattedAmount = "✨ " + formattedAmount + "!!! \uD83D\uDE0D\uD83D\uDCA5";

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
        embedBuilder.setTitle("Love Calculator");

        embedBuilder.addField("\uD83D\uDC65 People",
                user1.getAsMention() + " & " + user2.getAsMention(),
                false);

        embedBuilder.addField("❤️\u200D\uD83D\uDD25 Match",
                formattedAmount,
                false);

        return embedBuilder.build();
    }
}

package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MagicBall
{

    public static LinkedList<String> getLabels()
    {
        return new LinkedList<>(Arrays.asList("8ball", "8b", "eightball", "magicball"));
    }

    private final static List<String> answers = new ArrayList<>(
            Arrays.asList("It is certain.",
                    "It is decidedly so.",
                    "Without a doubt.",
                    "Yes, definitely.",
                    "That would be a yes.",
                    "As I see it, yes.",
                    "Most likely.",
                    "Looks like it.",
                    "Yes.",
                    "Signs point to yes.",
                    "Reply hazy, try again.",
                    "Ask again later.",
                    "Better not tell you now.",
                    "Seems uncertain.",
                    "Concentrate and ask again.",
                    "Don't count on it.",
                    "My answer is no.",
                    "My sources say no.",
                    "Outlook not so good.",
                    "Very doubtful."));

    public static String getRandomAnswer()
    {
        int answerPos = RandomUtil.getRandomNumber(0, answers.size() - 1);
        return answers.get(answerPos);
    }

    public static MessageEmbed generateEmbed(String question, User author)
    {
        // add a question mark at the end, if missing.
        // this might not always apply but it's fun
        if(!question.endsWith("?")) question += "?";

        String answer = getRandomAnswer();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
        embedBuilder.setTitle("Magic Ball");
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.addField("❓ Question", question, false);
        embedBuilder.addField("\uD83C\uDFB1 Answer", answer, false);

        return embedBuilder.build();
    }
}

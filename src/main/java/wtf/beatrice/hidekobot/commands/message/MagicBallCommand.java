package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MagicBallCommand implements MessageCommand
{
    private final List<String> answers = new ArrayList<>(
            Arrays.asList("It is certain.",
            "It is decidedly so.",
            "Without a doubt.",
            "Yes definitely.",
            "You may rely on it.",
            "As I see it, yes.",
            "Most likely.",
            "Outlook good.",
            "Yes.",
            "Signs point to yes.",
            "Reply hazy, try again.",
            "Ask again later.",
            "Better not tell you now.",
            "Cannot predict now.",
            "Concentrate and ask again.",
            "Don't count on it.",
            "My reply is no.",
            "My sources say no.",
            "Outlook not so good.",
            "Very doubtful."));

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Arrays.asList("8ball", "eightball", "magicball", "8b"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null; // anyone can use it
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        if(args.length == 0)
        {
            event.getMessage().reply("You need to specify a question!").queue();
            return;
        }

        StringBuilder questionBuilder = new StringBuilder();
        for(int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            questionBuilder.append(arg);
            if(i + 1 != args.length) // don't add a separator on the last iteration
                questionBuilder.append(" ");
        }

       String question = questionBuilder.toString();

        // add a question mark at the end, if missing.
        // this might not always apply but it's fun
        if(!question.endsWith("?")) question += "?";

       int answerPos = RandomUtil.getRandomNumber(0, answers.size() - 1);
       String answer = answers.get(answerPos);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        embedBuilder.setTitle("Magic Ball");
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.addField("❓ Question", question, false);
        embedBuilder.addField("\uD83C\uDFB1 Answer", answer, false);

        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }
}

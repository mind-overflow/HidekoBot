package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LoveCalculatorCommand implements MessageCommand
{


    @Override
    public LinkedList<String> getCommandLabels()
    {
        return new LinkedList<>(Arrays.asList("lovecalc", "lovecalculator", "lc"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null; //anyone can use it
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {

        Mentions mentionsObj = event.getMessage().getMentions();
        List<IMentionable> mentions = mentionsObj.getMentions();


        if(args.length == 0 || mentions.isEmpty())
        {
            event.getMessage().reply("\uD83D\uDE22 I need to know who to check!").queue();
            return;
        }

        User user1, user2;

        String mentionedUserId = mentions.get(0).getId();
        user1 = HidekoBot.getAPI().retrieveUserById(mentionedUserId).complete();

        if(mentions.size() == 1)
        {
            user2 = event.getAuthor();
        } else {
            mentionedUserId = mentions.get(1).getId();
            user2 = HidekoBot.getAPI().retrieveUserById(mentionedUserId).complete();
        }

        int loveAmount = RandomUtil.getRandomNumber(0, 100);

        String formattedAmount = loveAmount + "%";
        if(loveAmount <= 30) formattedAmount += "... \uD83D\uDE22";
        else if(loveAmount < 60) formattedAmount += "! \uD83E\uDDD0";
        else if(loveAmount < 75) formattedAmount += "!!! \uD83E\uDD73";
        else formattedAmount = "✨ " + formattedAmount + "!!! \uD83D\uDE0D\uD83D\uDCA5";

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        embedBuilder.setTitle("Love Calculator");

        embedBuilder.addField("\uD83D\uDC65 People",
                user1.getAsMention() + " & " + user2.getAsMention(),
                false);

        embedBuilder.addField("❤️\u200D\uD83D\uDD25 Match",
                formattedAmount,
                false);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

    }
}

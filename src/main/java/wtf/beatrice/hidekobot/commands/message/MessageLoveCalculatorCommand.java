package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.commands.base.LoveCalculator;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class MessageLoveCalculatorCommand implements MessageCommand
{
    private final LoveCalculator loveCalculator;

    public MessageLoveCalculatorCommand(@Autowired LoveCalculator loveCalculator)
    {
        this.loveCalculator = loveCalculator;
    }


    @Override
    public LinkedList<String> getCommandLabels()
    {
        return new LinkedList<>(Arrays.asList("lovecalc", "lovecalculator", "lc"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions()
    {
        return null; //anyone can use it
    }

    @Override
    public boolean passRawArgs()
    {
        return false;
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Calculate how much two people love each other. You can mention two people or just one.";
    }

    @Nullable
    @Override
    public String getUsage()
    {
        return "<person 1> [person 2]";
    }

    @NotNull
    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.FUN;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {

        Mentions mentionsObj = event.getMessage().getMentions();
        List<IMentionable> mentions = mentionsObj.getMentions();


        if (args.length == 0 || mentions.isEmpty())
        {
            event.getMessage()
                    .reply("\uD83D\uDE22 I need to know who to check! Please mention them.")
                    .queue();
            return;
        }

        User user1, user2;

        String mentionedUserId = mentions.get(0).getId();
        user1 = HidekoBot.getAPI().retrieveUserById(mentionedUserId).complete();

        if (mentions.size() == 1)
        {
            user2 = event.getAuthor();
        } else
        {
            mentionedUserId = mentions.get(1).getId();
            user2 = HidekoBot.getAPI().retrieveUserById(mentionedUserId).complete();
        }

        MessageEmbed embed = loveCalculator.buildEmbedAndCacheResult(event.getAuthor(), user1, user2);
        event.getChannel().sendMessageEmbeds(embed).queue();

    }
}

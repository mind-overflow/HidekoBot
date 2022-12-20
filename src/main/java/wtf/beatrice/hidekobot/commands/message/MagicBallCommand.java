package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.commands.base.MagicBall;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.LinkedList;
import java.util.List;

public class MagicBallCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return MagicBall.getLabels();
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

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
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


        event.getChannel().sendMessageEmbeds(MagicBall.generateEmbed(question, event.getAuthor())).queue();
    }
}

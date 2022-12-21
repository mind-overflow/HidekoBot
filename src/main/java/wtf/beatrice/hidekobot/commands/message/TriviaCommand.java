package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.commands.base.Trivia;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TriviaCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("trivia"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null;
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

    @NotNull
    @Override
    public String getDescription() {
        return "Start a Trivia session and play with others!";
    }

    @Nullable
    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        MessageChannel channel = event.getChannel();

        if(!(channel instanceof TextChannel))
        {
            channel.sendMessage(Trivia.getNoDMsError()).queue();
            return;
        }

        if(Trivia.channelsRunningTrivia.contains(channel.getId()))
        {
            // todo: also what if the bot stops (database...?)
            // todo: also what if the message is already deleted
            Message err = event.getMessage().reply(Trivia.getTriviaAlreadyRunningError()).complete();
            Cache.getTaskScheduler().schedule(() -> err.delete().queue(), 10, TimeUnit.SECONDS);
            return;
        }

        MessageResponse response = Trivia.generateMainScreen();

        event.getMessage().replyEmbeds(response.embed()).addActionRow(response.components()).queue(message ->
        {
            Cache.getDatabaseSource().trackRanCommandReply(message, event.getAuthor());
            Cache.getDatabaseSource().queueDisabling(message);
        });


    }
}

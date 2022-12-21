package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.runnables.TriviaTask;
import wtf.beatrice.hidekobot.util.TriviaUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
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
            channel.sendMessage("\uD83D\uDE22 Sorry! Trivia doesn't work in DMs.").queue();
            return;
        }

        if(TriviaUtil.channelsRunningTrivia.contains(channel.getId()))
        {
            // todo nicer looking
            // todo: also what if the bot stops (database...?)
            // todo: also what if the message is already deleted
            Message err = event.getMessage().reply("Trivia is already running here!").complete();
            Cache.getTaskScheduler().schedule(() -> err.delete().queue(), 10, TimeUnit.SECONDS);
            return;
        } else {
            // todo nicer looking
            event.getMessage().reply("Starting new Trivia session!").queue();
        }


        TriviaTask triviaTask = new TriviaTask(event.getAuthor(), channel);
        ScheduledFuture<?> future =
                Cache.getTaskScheduler().scheduleAtFixedRate(triviaTask,
                        0,
                        15,
                        TimeUnit.SECONDS);
        triviaTask.setScheduledFuture(future);

        TriviaUtil.channelsRunningTrivia.add(channel.getId());

    }
}

package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.TriviaQuestion;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.util.TriviaUtil;

import java.nio.channels.ScatteringByteChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

        if(TriviaUtil.channelsRunningTrivia.contains(event.getChannel().getId()))
        {
            // todo nicer looking
            Message err = event.getMessage().reply("Trivia is already running here!").complete();
            Cache.getTaskScheduler().schedule(() -> err.delete().queue(), 15, TimeUnit.SECONDS);
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("Trivia");

        JSONObject triviaJson = TriviaUtil.fetchTrivia();
        List<TriviaQuestion> questions = TriviaUtil.getQuestions(triviaJson); //todo null check, rate limiting...
        TriviaQuestion first = questions.get(0);

        embedBuilder.addField("Question", first.question(), false);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();



    }
}

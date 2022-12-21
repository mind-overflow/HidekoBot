package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.TriviaQuestion;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.util.TriviaUtil;

import java.util.ArrayList;
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
        Channel channel = event.getChannel();

        if(!(channel instanceof TextChannel))
        {
            event.getMessage().reply("\uD83D\uDE22 Sorry! Trivia doesn't work in DMs.").queue();
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


        JSONObject triviaJson = TriviaUtil.fetchTrivia();
        List<TriviaQuestion> questions = TriviaUtil.getQuestions(triviaJson); //todo null check, rate limiting...
        TriviaQuestion first = questions.get(0);

        List<Button> answerButtons = new ArrayList<>();

        Button correctAnswerButton = Button.primary("trivia_correct", first.correctAnswer());
        answerButtons.add(correctAnswerButton);

        int i = 0; // we need to add a number because buttons can't have the same id
        for(String wrongAnswer : first.wrongAnswers())
        {
            i++;
            Button wrongAnswerButton = Button.primary("trivia_wrong_" + i, wrongAnswer);
            answerButtons.add(wrongAnswerButton);
        }

        Collections.shuffle(answerButtons);

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("Trivia");

        embedBuilder.addField("Question", first.question(), false);

        Message botMessage = event.getChannel()
                .sendMessageEmbeds(embedBuilder.build())
                .setActionRow(answerButtons)
                .complete();

        Cache.getDatabaseSource().trackRanCommandReply(botMessage, event.getAuthor());
        // todo: ^ we should get rid of this tracking, since we don't need to know who started the trivia.
        // todo: however, for now, that's the only way to avoid a thread-locking scenario as some data is
        // todo: only stored in that table. this should be solved when we merge / fix the two main tables.
        // todo: then, we can remove this instruction.
        Cache.getDatabaseSource().queueDisabling(botMessage);

        TriviaUtil.channelsRunningTrivia.add(event.getChannel().getId());

    }
}

package wtf.beatrice.hidekobot.runnables;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONObject;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.TriviaQuestion;
import wtf.beatrice.hidekobot.util.CommandUtil;
import wtf.beatrice.hidekobot.util.TriviaUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class TriviaTask implements Runnable
{

    private final User author;
    private final MessageChannel channel;

    private Message previousMessage = null;

    private final JSONObject triviaJson;
    private final List<TriviaQuestion> questions;

    ScheduledFuture<?> future = null;

    private int iteration = 0;

    public TriviaTask(User author, MessageChannel channel)
    {
        this.author = author;
        this.channel = channel;

        triviaJson = TriviaUtil.fetchTrivia();
        questions = TriviaUtil.getQuestions(triviaJson); //todo: null check, rate limiting...
    }

    public void setScheduledFuture(ScheduledFuture<?> future)
    {
        this.future = future;
    }

    @Override
    public void run() {

        if(previousMessage != null)
        {
            // todo: we shouldn't use this method, since it messes with the database...
            CommandUtil.disableExpired(previousMessage.getId());
            String previousCorrectAnswer = questions.get(iteration-1).correctAnswer();
            previousMessage.reply("The correct answer was: **" + previousCorrectAnswer + "**!").queue();
            // todo: maybe also add who replied correctly as a list
        }

        if(iteration >= questions.size())
        {
            // todo: nicer-looking embed with stats
            channel.sendMessage("Trivia session is over!").queue();

            TriviaUtil.channelsRunningTrivia.remove(channel.getId());
            future.cancel(false);
            // we didn't implement null checks on the future on purpose, because we need to know if we were unable
            // to cancel it (and console errors should make it clear enough).
            return;
        }

        TriviaQuestion currentTriviaQuestion = questions.get(iteration);

        List<Button> answerButtons = new ArrayList<>();

        Button correctAnswerButton = Button.primary("trivia_correct", currentTriviaQuestion.correctAnswer());
        answerButtons.add(correctAnswerButton);

        int i = 0; // we need to add a number because buttons can't have the same id
        for(String wrongAnswer : currentTriviaQuestion.wrongAnswers())
        {
            i++;
            Button wrongAnswerButton = Button.primary("trivia_wrong_" + i, wrongAnswer);
            answerButtons.add(wrongAnswerButton);
        }

        Collections.shuffle(answerButtons);

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("Trivia (" + (iteration+1) + "/" + questions.size() + ")");

        embedBuilder.addField("Question", currentTriviaQuestion.question(), false);

        previousMessage = channel
                .sendMessageEmbeds(embedBuilder.build())
                .setActionRow(answerButtons)
                .complete();

        Cache.getDatabaseSource().trackRanCommandReply(previousMessage, author);
        // todo: ^ we should get rid of this tracking, since we don't need to know who started the trivia.
        // todo: however, for now, that's the only way to avoid a thread-locking scenario as some data is
        // todo: only stored in that table. this should be solved when we merge / fix the two main tables.
        // todo: then, we can remove this instruction.
        Cache.getDatabaseSource().queueDisabling(previousMessage);

        iteration++;
    }
}

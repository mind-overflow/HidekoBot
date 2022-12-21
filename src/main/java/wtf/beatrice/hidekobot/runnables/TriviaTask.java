package wtf.beatrice.hidekobot.runnables;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONObject;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.fun.TriviaCategory;
import wtf.beatrice.hidekobot.objects.fun.TriviaQuestion;
import wtf.beatrice.hidekobot.objects.fun.TriviaScore;
import wtf.beatrice.hidekobot.objects.comparators.TriviaScoreComparator;
import wtf.beatrice.hidekobot.util.CommandUtil;
import wtf.beatrice.hidekobot.util.TriviaUtil;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class TriviaTask implements Runnable
{
    private final User author;
    private final MessageChannel channel;

    private Message previousMessage = null;

    private final JSONObject triviaJson;
    private final List<TriviaQuestion> questions;
    private final TriviaCategory category;

    ScheduledFuture<?> future = null;

    private int iteration = 0;

    public TriviaTask(User author, MessageChannel channel, TriviaCategory category)
    {
        this.author = author;
        this.channel = channel;
        this.category = category;

        triviaJson = TriviaUtil.fetchJson(TriviaUtil.getTriviaLink(category.categoryId()));
        questions = TriviaUtil.parseQuestions(triviaJson); //todo: null check, rate limiting...
    }

    public void setScheduledFuture(ScheduledFuture<?> future)
    {
        this.future = future;
    }

    @Override
    public void run()
    {

        if(previousMessage != null)
        {
            // todo: we shouldn't use this method, since it messes with the database... look at coin reflip
            CommandUtil.disableExpired(previousMessage.getId());

            String previousCorrectAnswer = questions.get(iteration-1).correctAnswer();

            // we need this to be thread-locking to avoid getting out of sync with the rest of the trivia features
            previousMessage.reply("The correct answer was: **" + previousCorrectAnswer + "**!").complete();
            // todo: maybe also add who replied correctly as a list

            // clean the list of people who answered, so they can answer again for the new question
            TriviaUtil.channelAndWhoResponded.put(previousMessage.getChannel().getId(), new ArrayList<>());
        }

        if(iteration >= questions.size())
        {

            String scoreboardText = "\uD83D\uDC23 Trivia session is over!";

            List<String> winners = new ArrayList<>();
            int topScore = 0;
            StringBuilder othersBuilder = new StringBuilder();

            LinkedList<TriviaScore> triviaScores = TriviaUtil.channelAndScores.get(channel.getId());
            if(triviaScores == null) triviaScores = new LinkedList<>();
            else triviaScores.sort(new TriviaScoreComparator());

            int pos = 0;
            Integer previousScore = null;
            for(TriviaScore triviaScore : triviaScores)
            {
                if(pos > 10) break; // cap at top 10

                String user = triviaScore.getUser().getAsMention();
                int score = triviaScore.getScore();
                if(previousScore == null)
                {
                    previousScore = score;
                    topScore = score;
                    pos = 1;
                } else {
                    if(score != previousScore) pos++;
                }

                if(pos == 1) winners.add(user);
                else {
                    othersBuilder.append("\n").append(pos)
                            .append(" | ").append(user)
                            .append(": ").append(score).append(" points");
                }
            }

            StringBuilder winnersBuilder = new StringBuilder();
            for(int i = 0; i < winners.size(); i++)
            {
                String winner = winners.get(i);
                winnersBuilder.append(winner);
                if(i + 1 != winners.size())
                {
                    winnersBuilder.append(", "); // separate with comma except on last run
                } else {
                    winnersBuilder.append(": ").append(topScore).append(" points \uD83C\uDF89");
                }
            }

            String winnersTitle = "\uD83D\uDCAB ";
            winnersTitle += winners.size() == 1 ? "Winner" : "Winners";

            String winnersString = winnersBuilder.toString();
            String othersString = othersBuilder.toString();

            EmbedBuilder scoreboardBuilder = new EmbedBuilder();
            scoreboardBuilder.setColor(Cache.getBotColor());
            scoreboardBuilder.setTitle("\uD83C\uDF1F Trivia Scoreboard");
            if(!winnersString.isEmpty()) scoreboardBuilder.addField(winnersTitle, winnersString, false);
            else scoreboardBuilder.addField("\uD83D\uDE22 Sad Trivia",
                    "No one played \uD83D\uDE2D", false);
            if(!othersString.isEmpty()) scoreboardBuilder.addField("☁️ Others", othersString, false);

            channel.sendMessage(scoreboardText).addEmbeds(scoreboardBuilder.build()).queue();

            // remove all cached data
            TriviaUtil.channelsRunningTrivia.remove(channel.getId());
            TriviaUtil.channelAndWhoResponded.remove(channel.getId());
            TriviaUtil.channelAndScores.remove(channel.getId());

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

        List<String> buttonEmojis = Arrays.asList("\uD83D\uDD34", "\uD83D\uDD35",
                "\uD83D\uDFE2", "\uD83D\uDFE1", "\uD83D\uDFE4", "\uD83D\uDFE3", "\uD83D\uDFE0");

        // add emojis to buttons
        for(int emojiPos = 0; emojiPos < buttonEmojis.size(); emojiPos++)
        {
            if(emojiPos == answerButtons.size()) break;

            String emoji = buttonEmojis.get(emojiPos);
            Button button = answerButtons.get(emojiPos);

            answerButtons.set(emojiPos, button.withEmoji(Emoji.fromUnicode(emoji)));
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("\uD83C\uDFB2 Trivia - " + category.categoryName() +
                " (" + (iteration+1) + "/" + questions.size() + ")");

        embedBuilder.addField("❓ Question", currentTriviaQuestion.question(), false);

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

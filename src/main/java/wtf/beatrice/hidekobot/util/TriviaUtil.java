package wtf.beatrice.hidekobot.util;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.TriviaQuestion;
import wtf.beatrice.hidekobot.objects.TriviaScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TriviaUtil
{
    private final static String link = "https://opentdb.com/api.php?amount=10&type=multiple";

    public static List<String> channelsRunningTrivia = new ArrayList<>();

    // first string is the channelId, the list contain all users who responded there
    public static HashMap<String, List<String>> channelAndWhoResponded = new HashMap<>();

    // first string is the channelId, the list contain all score records for that channel
    public static HashMap<String, List<TriviaScore>> channelAndScores = new HashMap<>();

    public static JSONObject fetchTrivia()
    {
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String currentChar;
            StringBuilder jsonStrBuilder = new StringBuilder();
            while((currentChar = bufferedReader.readLine()) != null)
            {
                jsonStrBuilder.append(currentChar);
            }
            bufferedReader.close();
            return new JSONObject(jsonStrBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<TriviaQuestion> getQuestions(JSONObject jsonObject)
    {
        List<TriviaQuestion> questions = new ArrayList<>();

        JSONArray results = jsonObject.getJSONArray("results");

        for(Object currentQuestionGeneric : results)
        {
            JSONObject questionJson = (JSONObject) currentQuestionGeneric;
            String question = StringEscapeUtils.unescapeHtml4(questionJson.getString("question"));
            String correctAnswer = StringEscapeUtils.unescapeHtml4(questionJson.getString("correct_answer"));

            List<String> incorrectAnswersList = new ArrayList<>();

            JSONArray incorrectAnswers = questionJson.getJSONArray("incorrect_answers");
            for(Object incorrectAnswerGeneric : incorrectAnswers)
            {
                String incorrectAnswer = (String) incorrectAnswerGeneric;
                incorrectAnswersList.add(StringEscapeUtils.unescapeHtml4(incorrectAnswer));
            }

            TriviaQuestion triviaQuestion = new TriviaQuestion(question, correctAnswer, incorrectAnswersList);
            questions.add(triviaQuestion);
        }

        return questions;
    }

    public static void handleAnswer(ButtonInteractionEvent event, AnswerType answerType)
    {
        User user = event.getUser();
        String channelId = event.getChannel().getId();

        if(trackResponse(user, event.getChannel()))
        {
            List<TriviaScore> scores = channelAndScores.get(channelId);
            if(scores == null) scores = new ArrayList<>();
            TriviaScore currentUserScore = null;
            for(TriviaScore score : scores)
            {
                if(score.getUser().equals(user))
                {
                    currentUserScore = score;
                    scores.remove(score);
                    break;
                }
            }

            if(currentUserScore == null)
            {
                currentUserScore = new TriviaScore(user);
            }

            if(answerType.equals(AnswerType.CORRECT))
            {

                event.reply(user.getAsMention() + " got it right! \uD83E\uDD73 (**+3**)").queue();
                currentUserScore.changeScore(3);

            } else {
                event.reply("❌ " + user.getAsMention() + ", that's not the right answer! (**-1**)").queue();
                currentUserScore.changeScore(-1);
            }

            scores.add(currentUserScore);
            channelAndScores.put(channelId, scores);
        } else {
            event.reply("☹️ " + user.getAsMention() + ", you can't answer twice!")
                    .queue(interaction ->
                    Cache.getTaskScheduler().schedule(() ->
                            interaction.deleteOriginal().queue(), 3, TimeUnit.SECONDS));
        }
    }

    private static boolean trackResponse(User user, MessageChannel channel)
    {
        String userId = user.getId();
        String channelId = channel.getId();

        List<String> responders = channelAndWhoResponded.get(channelId);

        if(responders == null)
        {
            responders = new ArrayList<>();
        }

        if(responders.isEmpty() || !responders.contains(userId))
        {
            responders.add(userId);
            channelAndWhoResponded.put(channelId, responders);
            return true; // response was successfully tracked
        } else {
            return false; // response wasn't tracked because there already was an entry
        }
    }

    public enum AnswerType {
        CORRECT, WRONG
    }

}

package wtf.beatrice.hidekobot.util;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.jsoup.Jsoup;
import wtf.beatrice.hidekobot.objects.TriviaQuestion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class TriviaUtil
{
    private final static String link = "https://opentdb.com/api.php?amount=10&type=multiple";

    public static List<String> channelsRunningTrivia = new ArrayList<>();

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

}

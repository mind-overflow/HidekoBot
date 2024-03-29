package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.objects.comparators.TriviaCategoryComparator;
import wtf.beatrice.hidekobot.objects.fun.TriviaCategory;
import wtf.beatrice.hidekobot.objects.fun.TriviaQuestion;
import wtf.beatrice.hidekobot.objects.fun.TriviaScore;
import wtf.beatrice.hidekobot.runnables.TriviaTask;
import wtf.beatrice.hidekobot.util.CommandUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Trivia
{

    private Trivia() {
        throw new IllegalStateException("Utility class");
    }

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Trivia.class);
    private static final String TRIVIA_API_LINK = "https://opentdb.com/api.php?amount=10&type=multiple&category=";
    private static final String TRIVIA_API_CATEGORIES_LINK = "https://opentdb.com/api_category.php";

    public static List<String> channelsRunningTrivia = new ArrayList<>();

    // first string is the channelId, the list contain all users who responded there
    public static HashMap<String, List<String>> channelAndWhoResponded = new HashMap<>();

    // first string is the channelId, the list contain all score records for that channel
    public static HashMap<String, LinkedList<TriviaScore>> channelAndScores = new HashMap<>();

    public static String getTriviaLink(int categoryId) {return TRIVIA_API_LINK + categoryId; }
    public static String getCategoriesLink() {return TRIVIA_API_CATEGORIES_LINK; }

    public static String getNoDMsError() {
        return "\uD83D\uDE22 Sorry! Trivia doesn't work in DMs.";
    }

    public static String getTriviaAlreadyRunningError() {
        // todo nicer looking
        return "Trivia is already running here!";
    }

    public static MessageResponse generateMainScreen()
    {
        JSONObject categoriesJson = Trivia.fetchJson(Trivia.getCategoriesLink());
        if(categoriesJson == null)
            return new MessageResponse("Error fetching trivia!", null); // todo nicer with emojis
        List<TriviaCategory> categories = Trivia.parseCategories(categoriesJson);
        if(categories.isEmpty())
            return new MessageResponse("Error parsing trivia categories!", null); // todo nicer with emojis

        categories.sort(new TriviaCategoryComparator());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("\uD83C\uDFB2 Trivia");
        embedBuilder.addField("\uD83D\uDCD6 Begin here",
                "Select a category from the dropdown menu to start a match!",
                false);
        embedBuilder.addField("❓ How to play",
                "A new question gets posted every few seconds." +
                        "\nIf you get it right, you earn points!" +
                        "\nIf you choose a wrong answer, you lose points." +
                        "\nIf you are unsure, you can wait without answering and your score won't change!",
                false);

        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("trivia_categories");

        for(TriviaCategory category : categories)
        {
            String name = category.categoryName();
            int id = category.categoryId();
            menuBuilder.addOption(name, String.valueOf(id));
        }

        return new MessageResponse(null, embedBuilder.build(), menuBuilder.build());
    }

    public static JSONObject fetchJson(String link)
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
            LOGGER.error("JSON Parsing Exception", e);
        }

        return null;
    }

    public static List<TriviaQuestion> parseQuestions(JSONObject jsonObject)
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

    public static List<TriviaCategory> parseCategories(JSONObject jsonObject)
    {
        List<TriviaCategory> categories = new ArrayList<>();
        JSONArray categoriesArray = jsonObject.getJSONArray("trivia_categories");
        for(Object categoryObject : categoriesArray)
        {
            JSONObject categoryJson = (JSONObject) categoryObject;

            String name = categoryJson.getString("name");
            int id = categoryJson.getInt("id");

            categories.add(new TriviaCategory(name, id));
        }

        return categories;
    }

    public static void handleAnswer(ButtonInteractionEvent event, AnswerType answerType)
    {
        User user = event.getUser();
        String channelId = event.getChannel().getId();

        if(trackResponse(user, event.getChannel()))
        {
            LinkedList<TriviaScore> scores = channelAndScores.get(channelId);
            if(scores == null) scores = new LinkedList<>();
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

    public static void handleMenuSelection(StringSelectInteractionEvent event)
    {
        // check if the user interacting is the same one who ran the command
        if(!(Cache.getDatabaseSource().isUserTrackedFor(event.getUser().getId(), event.getMessageId())))
        {
            event.reply("❌ You did not run this command!").setEphemeral(true).queue();
            return;
        }

        // todo: we shouldn't use this method, since it messes with the database... look at coin reflip
        CommandUtil.disableExpired(event.getMessageId());

        SelectOption pickedOption = event.getInteraction().getSelectedOptions().get(0);
        String categoryName = pickedOption.getLabel();
        String categoryIdString = pickedOption.getValue();
        Integer categoryId = Integer.parseInt(categoryIdString);

        TriviaCategory category = new TriviaCategory(categoryName, categoryId);

        startTrivia(event, category);
    }

    public static void startTrivia(StringSelectInteractionEvent event, TriviaCategory category)
    {
        User author = event.getUser();
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel();

        if(Trivia.channelsRunningTrivia.contains(channel.getId()))
        {
            // todo nicer looking
            // todo: also what if the bot stops (database...?)
            // todo: also what if the message is already deleted
            Message err = event.reply("Trivia is already running here!").complete().retrieveOriginal().complete();
            Cache.getTaskScheduler().schedule(() -> err.delete().queue(), 10, TimeUnit.SECONDS);
            return;
        } else {
            // todo nicer looking
            event.reply("Starting new Trivia session!").queue();
        }


        TriviaTask triviaTask = new TriviaTask(author, channel, category);
        ScheduledFuture<?> future =
                Cache.getTaskScheduler().scheduleAtFixedRate(triviaTask,
                        0,
                        15,
                        TimeUnit.SECONDS);
        triviaTask.setScheduledFuture(future);

        Trivia.channelsRunningTrivia.add(channel.getId());
    }

    public enum AnswerType {
        CORRECT, WRONG
    }

}

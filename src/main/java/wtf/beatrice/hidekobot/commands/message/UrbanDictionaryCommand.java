package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.io.*;
import java.util.*;

public class UrbanDictionaryCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Arrays.asList("urban", "urbandictionary", "ud"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null; //anyone can use it
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    static final String baseUrl = "https://www.urbandictionary.com/define.php?term=";
    static final Button previousPageButton = Button.primary("urban_previouspage", "Back")
            .withEmoji(Emoji.fromFormatted("⬅️"));
    static final Button nextPageButton = Button.primary("urban_nextpage", "Next")
            .withEmoji(Emoji.fromFormatted("➡️"));

    private static MessageEmbed buildEmbed(String term,
                                           String url,
                                           User author,
                                           String meaning,
                                           String example,
                                           String contributor,
                                           String date,
                                           int page)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle(term + ", on Urban Dictionary", url);
        embedBuilder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
        embedBuilder.addField("Definition", meaning, false);
        embedBuilder.addField("Example", example, false);
        embedBuilder.addField("Submission",
                "*Entry " + (page+1) + " | Sent by " + contributor + " on " + date + "*",
                false);

        return embedBuilder.build();
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        if(args.length == 0)
        {
            event.getMessage().reply("\uD83D\uDE22 I need to know what to search for!").queue();
            return;
        }


        // sanitize args by only keeping letters and numbers, and adding "+" instead of spaces for HTML parsing
        StringBuilder termBuilder = new StringBuilder();
        for(int pos = 0; pos < args.length; pos++)
        {
            String arg = args[pos];

            arg = arg.replaceAll("[^\\d\\w]", "");
            termBuilder.append(arg);

            if(pos + 1 != args.length)
                termBuilder.append("+"); // add everywhere except on last iteration
        }

        String term = termBuilder.toString();

        // cut it to length to avoid abuse
        if (term.length() > 64) term = term.substring(0, 64);

        String url = baseUrl + term;

        Document doc;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            event.getMessage().reply("\uD83D\uDE22 I couldn't find that term!").queue();
            return;
        }

        List<String> plaintextMeanings = new ArrayList<>();
        List<String> plaintextExamples = new ArrayList<>();
        List<String> contributorsNames = new ArrayList<>();
        List<String> submissionDates = new ArrayList<>();

        Elements definitions = doc.getElementsByClass("definition");
        for(Element definition : definitions)
        {
            Elements meaningSingleton = definition.getElementsByClass("meaning");
            if(meaningSingleton.isEmpty())
            {
                plaintextMeanings.add(" ");
            } else
            {
                Element meaning = meaningSingleton.get(0);
                String text = meaning.html()
                        .replaceAll("<br\\s*?>", "\n") // keep newlines
                        .replaceAll("<.*?>", ""); // remove all other html tags
                // this is used to fix eg. &amp; being shown literally instead of being parsed
                text = StringEscapeUtils.unescapeHtml4(text);
                // discord only allows 1024 characters for embed fields
                if(text.length() > 1024) text = text.substring(0, 1020) + "...";
                plaintextMeanings.add(text);
            }

            Elements exampleSingleton = definition.getElementsByClass("example");

            if(exampleSingleton.isEmpty())
            {
                plaintextExamples.add(" ");
            } else
            {
                Element example = exampleSingleton.get(0);
                String text = example.html()
                        .replaceAll("<br\\s*?>", "\n") // keep newlines
                        .replaceAll("<.*?>", ""); // remove all other html tags
                // this is used to fix eg. &amp; being shown literally instead of being parsed
                text = StringEscapeUtils.unescapeHtml4(text);
                // discord only allows 1024 characters for embed fields
                if(text.length() > 1024) text = text.substring(0, 1020) + "...";
                plaintextExamples.add(text);
            }

            Elements contributorSingleton = definition.getElementsByClass("contributor");
            if(contributorSingleton.isEmpty())
            {
                contributorsNames.add("Unknown");
            } else
            {
                Element contributor = contributorSingleton.get(0);

                String htmlContributor = contributor.html();
                String htmlContributorName = contributor.select("a").html();
                String htmlSubmitDate = htmlContributor.substring(
                        htmlContributor.indexOf("</a>") + 4);

                contributorsNames.add(htmlContributorName
                        .replaceAll("<.*?>", "")); // remove all html tags;

                submissionDates.add(htmlSubmitDate
                        .replaceAll("<.*?>", "")); // remove all html tags;
            }
        }

        // make it nice to look at, compared to the html value

        final String finalTerm = term;
        term = WordUtils.capitalizeFully(term.replaceAll("\\+", " "));

        String serializedMeanings = serialize(plaintextMeanings);
        String serializedExamples = serialize(plaintextExamples);
        String serializedContributors = serialize(contributorsNames);
        String serializedDates = serialize(submissionDates);

        // disable next page if we only have one result
        Button nextPageBtnLocal = nextPageButton;
        if(submissionDates.size() == 1) nextPageBtnLocal = nextPageBtnLocal.asDisabled();

        MessageEmbed embed = buildEmbed(term, url, event.getAuthor(), plaintextMeanings.get(0),
                plaintextExamples.get(0), contributorsNames.get(0), submissionDates.get(0), 0);

        // copy term for async thing
        event.getChannel()
                .sendMessageEmbeds(embed)
                .addActionRow(previousPageButton.asDisabled(), //disabled by default because we're on page 0
                        nextPageBtnLocal)
                .queue(message ->
        {

            Cache.getDatabaseSource().queueDisabling(message);
            Cache.getDatabaseSource().trackRanCommandReply(message, event.getAuthor());
            Cache.getDatabaseSource().trackUrban(serializedMeanings,
                    serializedExamples,
                    serializedContributors,
                    serializedDates,
                    message,
                    finalTerm);
        });

    }

    private String serialize(List dataList) {

        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
             ObjectOutputStream so = new ObjectOutputStream(bo)) {
            so.writeObject(dataList);
            so.flush();
            return Base64.getEncoder().encodeToString(bo.toByteArray());
        }
        catch (IOException ignored) {}
        return null;
    }

    private static ArrayList deserialize(String dataStr) {

        byte[] b = Base64.getDecoder().decode(dataStr);
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si;
        try {
            si = new ObjectInputStream(bi);
            return ArrayList.class.cast(si.readObject());
        }
        catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Error during deserialization", e);
        }
    }


    public static void changePage(ButtonInteractionEvent event, boolean increase)
    {
        String messageId = event.getMessageId();
        DatabaseSource database = Cache.getDatabaseSource();

        // check if the user interacting is the same one who ran the command
        if (!(database.isUserTrackedFor(event.getUser().getId(), messageId))) {
            event.reply("❌ You did not run this command!").setEphemeral(true).queue();
            return;
        }

        // get current page and calculate how many pages there are
        int page = Cache.getDatabaseSource().getUrbanPage(messageId);
        int pages;


        String serializedMeanings = database.getUrbanMeanings(messageId);
        List<String> meanings = deserialize(serializedMeanings);
        String serializedExamples = database.getUrbanExamples(messageId);
        List<String> examples = deserialize(serializedExamples);
        String serializedContributors = database.getUrbanContributors(messageId);
        List<String> contributors = deserialize(serializedContributors);
        String serializedDates = database.getUrbanDates(messageId);
        List<String> dates = deserialize(serializedDates);
        String term = database.getUrbanTerm(messageId);
        String url = baseUrl + term;

        // count how many pages there are
        pages = meanings.size();

        // move to new page
        if(increase)
            page++;
        else page--;

        term = WordUtils.capitalizeFully(term.replaceAll("\\+", " "));

        MessageEmbed updatedEmbed = buildEmbed(term, url, event.getUser(),
                meanings.get(page), examples.get(page), contributors.get(page),
                dates.get(page), page);



        List<ItemComponent> components = new ArrayList<>();

        if(page > 0)
        {
            components.add(previousPageButton.asEnabled());
        } else {
            components.add(previousPageButton.asDisabled());
        }

        if(page + 1 == pages)
        {
            components.add(nextPageButton.asDisabled());
        } else {
            components.add(nextPageButton.asEnabled());
        }

        ActionRow currentRow = ActionRow.of(components);
        List<ActionRow> actionRows = new ArrayList<>(Collections.singletonList(currentRow));

        event.getMessage().editMessageEmbeds(updatedEmbed).complete();
        event.editComponents(actionRows).complete();
        database.setUrbanPage(messageId, page);
        database.resetExpiryTimestamp(messageId);

    }
}

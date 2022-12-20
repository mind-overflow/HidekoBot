package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.WordUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;

import java.io.*;
import java.util.*;

public class UrbanDictionary
{

    public static LinkedList<String> getCommandLabels()
    { return new LinkedList<>(Arrays.asList("urban", "urbandictionary", "ud")); }


    public static String getBaseUrl() {
        return "https://www.urbandictionary.com/define.php?term=";
    }

    public static Button getPreviousPageButton()
    {
        return Button.primary("urban_previouspage", "Back")
                .withEmoji(Emoji.fromFormatted("⬅️"));
    }

    public static Button getNextPageButton()
    {
        return Button.primary("urban_nextpage", "Next")
                .withEmoji(Emoji.fromFormatted("➡️"));
    }

    public static Button getDeleteButton()
    {
        return Button.danger("urban_delete", "Delete")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDDD1️"));
    }

    public static String getNoArgsError() {
        return "\uD83D\uDE22 I need to know what to search for!";
    }

    public static String sanitizeArgs(String term, boolean forUrl)
    {
        term = term.replaceAll("[^\\d\\w\\s]", ""); // only keep letters, numbers and spaces
        term = WordUtils.capitalizeFully(term); // Make Every Word Start With A Capital Letter
        if(forUrl) term = term.replaceAll("\\s+", "+"); // replace all whitespaces with + for the url
        if (term.length() > 64) term = term.substring(0, 64); // cut it to length to avoid abuse
        return term;
    }

    public static String generateUrl(String term)
    {
        return getBaseUrl() + sanitizeArgs(term, true);
    }

    public static MessageEmbed buildEmbed(String term,
                                   String url,
                                   User author,
                                   UrbanSearch search,
                                   int page)
    {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle(term + ", on Urban Dictionary", url);
        embedBuilder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
        embedBuilder.addField("Definition", search.getPlaintextMeanings().get(page), false);
        embedBuilder.addField("Example", search.getPlaintextExamples().get(page), false);
        embedBuilder.addField("Submission",
                "*Entry " + (page+1) + " | Sent by " + search.getContributorsNames().get(page) +
                        " on " + search.getSubmissionDates().get(page) + "*",
                false);

        return embedBuilder.build();
    }

    public static String serialize(List dataList) {

        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
             ObjectOutputStream so = new ObjectOutputStream(bo)) {
            so.writeObject(dataList);
            so.flush();
            return Base64.getEncoder().encodeToString(bo.toByteArray());
        }
        catch (IOException ignored) {}
        return null;
    }

    public static LinkedList deserialize(String dataStr) {

        byte[] b = Base64.getDecoder().decode(dataStr);
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si;
        try {
            si = new ObjectInputStream(bi);
            return LinkedList.class.cast(si.readObject());
        }
        catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Error during deserialization", e);
        }
    }

    public static String getNoTermFoundError()
    {
        return "\uD83D\uDE22 I couldn't find that term!";
    }


    public static void delete(ButtonInteractionEvent event)
    {
        String messageId = event.getMessageId();
        DatabaseSource database = Cache.getDatabaseSource();

        // check if the user interacting is the same one who ran the command
        if (!(database.isUserTrackedFor(event.getUser().getId(), messageId))) {
            event.reply("❌ You did not run this command!").setEphemeral(true).queue();
            return;
        }

        event.getInteraction().getMessage().delete().queue();
    }

    public static void track(Message message, User user, UrbanSearch search, String sanitizedTerm)
    {
        Cache.getDatabaseSource().queueDisabling(message);
        Cache.getDatabaseSource().trackRanCommandReply(message, user);
        Cache.getDatabaseSource().trackUrban(search.getSerializedMeanings(),
                search.getSerializedExamples(),
                search.getSerializedContributors(),
                search.getSerializedDates(),
                message,
                sanitizedTerm);
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

        String term = database.getUrbanTerm(messageId);
        String url = generateUrl(term);

        // get serialized parameters
        String serializedMeanings = database.getUrbanMeanings(messageId);
        String serializedExamples = database.getUrbanExamples(messageId);
        String serializedContributors = database.getUrbanContributors(messageId);
        String serializedDates = database.getUrbanDates(messageId);

        // construct object by passing serialized parameters
        UrbanSearch search = new UrbanSearch(serializedMeanings,
                serializedExamples, serializedContributors, serializedDates);

        // move to new page
        if(increase)
            page++;
        else page--;

        term = UrbanDictionary.sanitizeArgs(term, false);

        // generate embed with new results
        MessageEmbed updatedEmbed = UrbanDictionary.buildEmbed(term, url, event.getUser(), search, page);

        // get all attached components and check which ones need to be enabled or disabled
        List<ItemComponent> components = new ArrayList<>();

        if(page > 0)
        {
            components.add(UrbanDictionary.getPreviousPageButton().asEnabled());
        } else {
            components.add(UrbanDictionary.getPreviousPageButton().asDisabled());
        }

        if(page + 1 == search.getPages())
        {
            components.add(UrbanDictionary.getNextPageButton().asDisabled());
        } else {
            components.add(UrbanDictionary.getNextPageButton().asEnabled());
        }

        // update the components on the object
        components.add(UrbanDictionary.getDeleteButton());
        ActionRow currentRow = ActionRow.of(components);

        // update the message
        event.editComponents(currentRow).setEmbeds(updatedEmbed).queue();
        database.setUrbanPage(messageId, page);
        database.resetExpiryTimestamp(messageId);
    }

    public static class UrbanSearch
    {
        final LinkedList<String> plaintextMeanings;
        final LinkedList<String> plaintextExamples;
        final LinkedList<String> contributorsNames;
        final LinkedList<String> submissionDates;

        final String serializedMeanings;
        final String serializedExamples;
        final String serializedContributors;
        final String serializedDates;

        final int pages;

        public UrbanSearch(String serializedMeanings,
                           String serializedExamples,
                           String serializedContributors,
                           String serializedDates)
        {
            this.serializedMeanings = serializedMeanings;
            this.serializedExamples = serializedExamples;
            this.serializedContributors = serializedContributors;
            this.serializedDates = serializedDates;

            this.plaintextMeanings = UrbanDictionary.deserialize(serializedMeanings);
            this.plaintextExamples = UrbanDictionary.deserialize(serializedExamples);
            this.contributorsNames = UrbanDictionary.deserialize(serializedContributors);
            this.submissionDates = UrbanDictionary.deserialize(serializedDates);

            this.pages = submissionDates.size();
        }

        public UrbanSearch(Elements definitions)
        {
            plaintextMeanings = new LinkedList<>();
            plaintextExamples = new LinkedList<>();
            contributorsNames = new LinkedList<>();
            submissionDates = new LinkedList<>();

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

            serializedMeanings = serialize(plaintextMeanings);
            serializedExamples = serialize(plaintextExamples);
            serializedContributors = serialize(contributorsNames);
            serializedDates = serialize(submissionDates);

            pages = submissionDates.size();
        }

        public List<String> getPlaintextMeanings() {
            return this.plaintextMeanings;
        }

        public List<String> getPlaintextExamples() {
            return this.plaintextExamples;
        }

        public List<String> getContributorsNames() {
            return this.contributorsNames;
        }

        public List<String> getSubmissionDates() {
            return this.submissionDates;
        }

        public String getSerializedMeanings() {
            return serializedMeanings;
        }

        public String getSerializedExamples() {
            return serializedExamples;
        }

        public String getSerializedContributors() {
            return serializedContributors;
        }

        public String getSerializedDates() {
            return serializedDates;
        }

        public int getPages() {
            return pages;
        }
    }

}
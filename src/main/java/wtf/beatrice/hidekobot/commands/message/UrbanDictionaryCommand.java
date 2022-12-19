package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

        String url = "https://www.urbandictionary.com/define.php?term=" + term;

        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            event.getMessage().reply("\uD83D\uDE22 I couldn't find that term!").queue();
            return;
        }

        List<String> contributorsNames = new ArrayList<>();
        List<String> submissionDates = new ArrayList<>();
        List<String> plaintextMeanings = new ArrayList<>();
        List<String> plaintextExamples = new ArrayList<>();

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
        term = term.replaceAll("\\+", " ");
        term = WordUtils.capitalizeFully(term);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle(term + ", on Urban Dictionary", url);
        embedBuilder.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        embedBuilder.addField("Definition", plaintextMeanings.get(0), false);
        embedBuilder.addField("Example", plaintextExamples.get(0), false);
        embedBuilder.addField("Submission",
                "*sent by " + contributorsNames.get(0) + " on " + submissionDates.get(0) + "*",
                false);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}

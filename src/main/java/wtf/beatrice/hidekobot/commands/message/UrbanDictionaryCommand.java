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

        List<String> htmlMeanings = new ArrayList<>();
        List<String> htmlExamples = new ArrayList<>();

        Elements definitions = doc.getElementsByClass("definition");
        for(Element element : definitions)
        {
            Elements meanings = element.getElementsByClass("meaning");
            for(Element meaning : meanings)
            {
                htmlMeanings.add(meaning.html());
                break;// just one meaning per definition
            }

            Elements examples = element.getElementsByClass("example");
            for(Element example : examples)
            {
                htmlExamples.add(example.html());
                break; // just one example per definition
            }
        }

        List<String> plaintextMeanings = new ArrayList<>();
        List<String> plaintextExamples = new ArrayList<>();

        for(String htmlMeaning : htmlMeanings)
        {
            String text = htmlMeaning
                    .replaceAll("<br\\s*?>", "\n") // keep newlines
                    .replaceAll("<.*?>", ""); // remove all other html tags
            // discord only allows 1024 characters for embed fields
            if(text.length() > 1024) text = text.substring(0, 1023);
            plaintextMeanings.add(text);
        }

        for(String htmlExample : htmlExamples)
        {
            String text = htmlExample
                    .replaceAll("<br\\s*?>", "\n") // keep newlines
                    .replaceAll("<.*?>", ""); // remove all other html tags
            // discord only allows 1024 characters for embed fields
            if(text.length() > 1024) text = text.substring(0, 1023);
            plaintextExamples.add(text);
        }

        // make it nice to look at, compared to the html value
        term = term.replaceAll("\\+", " ");
        term = WordUtils.capitalizeFully(term);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("Urban Dictionary: " + term);
        embedBuilder.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        embedBuilder.addField("Definition", plaintextMeanings.get(0), false);
        embedBuilder.addField("Example", plaintextExamples.get(0), false);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}

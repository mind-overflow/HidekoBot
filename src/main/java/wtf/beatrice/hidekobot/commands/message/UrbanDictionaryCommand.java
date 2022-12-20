package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.UrbanDictionary;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.io.*;
import java.util.*;

public class UrbanDictionaryCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return UrbanDictionary.getCommandLabels();
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
            event.getMessage().reply(UrbanDictionary.getNoArgsError()).queue();
            return;
        }

        // sanitize args by only keeping letters and numbers, and adding "+" instead of spaces for HTML parsing
        StringBuilder termBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            termBuilder.append(arg);

            if(i + 1 != args.length) // add spaces between args, but not on the last run
                termBuilder.append(" ");
        }

        String term = UrbanDictionary.sanitizeArgs(termBuilder.toString(), false);
        String url = UrbanDictionary.generateUrl(term);

        Document doc;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            event.getMessage().reply(UrbanDictionary.getNoTermFoundError()).queue();
            return;
        }

        Elements definitions = doc.getElementsByClass("definition");
        UrbanDictionary.UrbanSearch search = new UrbanDictionary.UrbanSearch(definitions);
        MessageEmbed embed = UrbanDictionary.buildEmbed(term, url, event.getAuthor(), search, 0);

        // disable next page if we only have one result
        Button nextPageBtnLocal = UrbanDictionary.getNextPageButton();
        if(search.getPages() == 1) nextPageBtnLocal = nextPageBtnLocal.asDisabled();

        event.getChannel()
                .sendMessageEmbeds(embed)
                .addActionRow(UrbanDictionary.getPreviousPageButton().asDisabled(),
                        //disabled by default because we're on page 0
                        nextPageBtnLocal,
                        UrbanDictionary.getDeleteButton())
                .queue(message -> UrbanDictionary.track(message, event.getAuthor(), search, term));

    }
}

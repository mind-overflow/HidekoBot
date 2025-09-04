package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import wtf.beatrice.hidekobot.commands.base.UrbanDictionary;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

import java.io.IOException;

public class UrbanDictionaryCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash(UrbanDictionary.getCommandLabels().get(0),
                        "Look up a term on Urban Dictionary.")
                .addOption(OptionType.STRING, "term", "The term to look up", true);
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        // get the term to look up
        OptionMapping textOption = event.getOption("term");
        String term = "";
        if (textOption != null)
        {
            term = textOption.getAsString();
        }

        if (textOption == null || term.isEmpty())
        {
            event.reply(UrbanDictionary.getNoArgsError())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        final String sanitizedTerm = UrbanDictionary.sanitizeArgs(term, false);
        String url = UrbanDictionary.generateUrl(sanitizedTerm);

        Document doc;

        try
        {
            doc = Jsoup.connect(url).get();
        } catch (IOException e)
        {
            event.reply(UrbanDictionary.getTermNotFoundError())
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Elements definitions = doc.getElementsByClass("definition");
        UrbanDictionary.UrbanSearch search = new UrbanDictionary.UrbanSearch(definitions);
        MessageEmbed embed = UrbanDictionary.buildEmbed(sanitizedTerm, url, event.getUser(), search, 0);

        // disable next page if we only have one result
        Button nextPageBtnLocal = UrbanDictionary.getNextPageButton();
        if (search.getPages() == 1) nextPageBtnLocal = nextPageBtnLocal.asDisabled();

        ActionRow actionRow = ActionRow.of(UrbanDictionary.getPreviousPageButton().asDisabled(),
                //disabled by default because we're on page 0
                nextPageBtnLocal,
                UrbanDictionary.getDeleteButton());
        event.getHook().editOriginalEmbeds(embed).setComponents(actionRow).queue(message ->
                UrbanDictionary.track(message, event.getUser(), search, sanitizedTerm));
    }
}

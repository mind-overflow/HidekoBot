package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.SlashCommand;

public class HelpCommand implements SlashCommand
{

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because replying might take a while
        event.deferReply().queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        // embed processing
        {
            embedBuilder.setColor(Cache.getBotColor());
            embedBuilder.setTitle("Help");

        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
}

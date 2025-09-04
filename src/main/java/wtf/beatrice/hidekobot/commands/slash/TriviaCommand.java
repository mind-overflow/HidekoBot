package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.Trivia;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class TriviaCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash("trivia",
                "Start a Trivia session and play with others!");
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        MessageChannel channel = event.getChannel();

        if (!(channel instanceof TextChannel))
        {
            event.reply(Trivia.getNoDMsError()).queue();
            return;
        }

        if (Trivia.channelsRunningTrivia.contains(channel.getId()))
        {
            event.reply(Trivia.getTriviaAlreadyRunningError()).setEphemeral(true).queue();
            return;
        }

        // if we got here, this might take a bit
        event.deferReply().queue();
        MessageResponse response = Trivia.generateMainScreen();

        event.getHook().editOriginalEmbeds(response.embed()).setActionRow(response.components()).queue(message ->
        {
            Cache.getServices().databaseService().trackRanCommandReply(message, event.getUser());
            Cache.getServices().databaseService().queueDisabling(message);
        });
    }
}

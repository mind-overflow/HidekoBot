package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.commands.base.MagicBall;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class SlashMagicBallCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash(MagicBall.getLabels().get(0),
                        "Ask a question to the magic ball.")
                .addOption(OptionType.STRING, "question",
                        "The question to ask.",
                        true,
                        false);
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // get the asked question
        OptionMapping textOption = event.getOption("question");
        String question = "";
        if (textOption != null)
        {
            question = textOption.getAsString();
        }

        if (textOption == null || question.isEmpty())
        {
            event.reply("\uD83D\uDE20 Hey, you have to ask me a question!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        MessageEmbed response = MagicBall.generateEmbed(question, event.getUser());
        event.replyEmbeds(response).queue();
    }
}

package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.commands.base.LoveCalculator;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

@Component
public class SlashLoveCalculatorCommand extends SlashCommandImpl
{
    private final LoveCalculator loveCalculator;

    public SlashLoveCalculatorCommand(@NotNull LoveCalculator loveCalculator)
    {
        this.loveCalculator = loveCalculator;
    }

    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash("lovecalc",
                        "Calculate how much two people love each other.")

                .addOption(OptionType.MENTIONABLE,
                        "first",
                        "The first person to account for",
                        true)

                .addOption(OptionType.MENTIONABLE,
                        "second",
                        "The second person to account for",
                        false);
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        User firstUser, secondUser;

        OptionMapping firsUserArg = event.getOption("first");
        if (firsUserArg != null)
        {
            firstUser = firsUserArg.getAsUser(); //todo null check?
        } else
        {
            event.reply("\uD83D\uDE22 I need to know who to check! Please mention them.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        OptionMapping secondUserArg = event.getOption("second");
        if (secondUserArg != null)
        {
            secondUser = secondUserArg.getAsUser(); //todo null check?
        } else
        {
            secondUser = event.getUser();
        }

        MessageEmbed embed = loveCalculator.buildEmbedAndCacheResult(event.getUser(), firstUser, secondUser);
        event.replyEmbeds(embed).queue();
    }
}

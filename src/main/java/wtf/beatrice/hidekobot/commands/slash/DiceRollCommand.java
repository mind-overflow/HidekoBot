package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.commands.base.DiceRoll;
import wtf.beatrice.hidekobot.commands.base.Say;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class DiceRollCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash("diceroll", "Roll dice. You can roll multiple dice at the same time.")
                .addOption(OptionType.STRING, "query",
                        "The dice to roll.",
                        false,
                        false);
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        OptionMapping textOption = event.getOption("query");
        String messageContent = "";
        if(textOption != null)
        {
             messageContent = textOption.getAsString();
        }

        String[] args = messageContent.split("\\s");

        MessageResponse response = DiceRoll.buildResponse(event.getUser(), args);

        if(response.getContent() != null)
        {
            event.getHook().editOriginal(response.getContent()).queue();
        } else if(response.getEmbed() != null)
        {
            event.getHook().editOriginalEmbeds(response.getEmbed()).queue();
        }
    }
}

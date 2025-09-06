package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.BotInfo;
import wtf.beatrice.hidekobot.objects.commands.SlashCommand;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

import java.util.LinkedList;
import java.util.List;

@Component
public class SlashBotInfoCommand extends SlashCommandImpl
{
    private final BotInfo botInfo;

    public SlashBotInfoCommand(@Autowired BotInfo botInfo)
    {
        this.botInfo = botInfo;
    }


    @Override
    public CommandData getSlashCommandData()
    {
        return Commands.slash("botinfo", "Get info about the bot.");
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because this might take a moment
        event.deferReply().queue();

        // get a list of slash commands
        List<SlashCommand> registeredCommands = Cache.getSlashCommandListener().getRegisteredCommands();
        LinkedList<String> registeredCommandNames = new LinkedList<>();
        for (SlashCommand command : registeredCommands)
        {
            // node: adding slash so people realize that this is specific about slash commands.
            registeredCommandNames.add("/" + command.getCommandName());
        }

        // send the list
        MessageEmbed embed = botInfo.generateEmbed(registeredCommandNames);
        event.getHook().editOriginalEmbeds(embed).queue();
    }
}

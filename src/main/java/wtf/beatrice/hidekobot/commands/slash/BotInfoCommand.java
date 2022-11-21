package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.utils.FormatUtil;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;

public class BotInfoCommand
{

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because this might take a moment
        event.deferReply().queue();

        List<Command> registeredCommands = Cache.getRegisteredCommands();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        // embed processing
        {
            embedBuilder.setColor(Cache.getBotColor());
            embedBuilder.setTitle(Cache.getBotName());

            // thumbnail
            String botAvatarUrl = HidekoBot.getAPI().getSelfUser().getAvatarUrl();
            if(botAvatarUrl != null) embedBuilder.setThumbnail(botAvatarUrl);

            // help field
            embedBuilder.addField("Getting started", "Type `/help` for help!", false);

            // commands list field
            StringBuilder commandsListBuilder = new StringBuilder();
            commandsListBuilder.append(registeredCommands.size()).append( " total - ");
            for(int i = 0; i < registeredCommands.size(); i++)
            {
                Command cmd = registeredCommands.get(i);
                commandsListBuilder.append("`" + cmd.getName() + "`");

                if(i + 1 != registeredCommands.size()) // don't add comma in last iteration
                {
                    commandsListBuilder.append(", ");
                }

            }
            embedBuilder.addField("Commands", commandsListBuilder.toString(), false);

            // version field
            embedBuilder.addField("Version", "v" + Cache.getBotVersion(), true);

            // jvm version field
            String jvmVersion = ManagementFactory.getRuntimeMXBean().getVmVersion();
            // only keep the important part "v19.0.1" and omit "v19.0.1+10"
            jvmVersion = jvmVersion.replaceAll("\\+.*", "");
            embedBuilder.addField("JVM Version", "v" + jvmVersion, true);

            // used ram field
            long usedRamBytes = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            double usedRamMB = usedRamBytes / 1024.0 / 1024.0; // bytes -> kB -> MB
            DecimalFormat ramMBFormatter = new DecimalFormat("#.##");
            embedBuilder.addField("RAM Usage", ramMBFormatter.format(usedRamMB) + " MB", true);

            // developer field
            String developerMention = "<@" + Cache.getBotMaintainerId() + ">";
            embedBuilder.addField("Maintainer", developerMention, true);

            // uptime field
            embedBuilder.addField("Uptime", FormatUtil.getNiceUptime(), true);

            // issue tracker field
            embedBuilder.addField("Support",
                    "[Issue tracker](https://git.beatrice.wtf/mind-overflow/HidekoBot/issues)",
                    true); //todo: we should probably make this a final field in the config class
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();


    }
}

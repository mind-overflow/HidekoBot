package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.HidekoBot;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BotInfoCommand
{

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because this might take a moment
        event.deferReply().queue();

        List<Command> registeredCommands = Configuration.getRegisteredCommands();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        // embed processing
        {
            embedBuilder.setColor(Configuration.getBotColor());
            embedBuilder.setTitle(Configuration.getBotName());

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
            embedBuilder.addField("Version", "v" + Configuration.getBotVersion(), true);

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

            // author field
            String authorMention = "<@" + Configuration.getBotOwnerId() + ">";
            embedBuilder.addField("Author", authorMention, true);

            // uptime field
            LocalDateTime now = LocalDateTime.now();
            long uptimeSeconds = ChronoUnit.SECONDS.between(Configuration.getStartupTime(), now);
            Duration uptime = Duration.ofSeconds(uptimeSeconds);
            long seconds = uptime.toSecondsPart();
            long minutes = uptime.toMinutesPart();
            long hours = uptime.toHoursPart();
            long days = uptime.toDays();

            StringBuilder uptimeStringBuilder = new StringBuilder();
            if(days == 0)
            {
                if(hours == 0)
                {
                    if(minutes == 0)
                    {} else {
                        uptimeStringBuilder.append(minutes).append("m ");
                    }
                } else {
                    uptimeStringBuilder.append(hours).append("h ");
                    uptimeStringBuilder.append(minutes).append("m ");
                }
            } else {
                uptimeStringBuilder.append(days).append("d ");
                uptimeStringBuilder.append(hours).append("h ");
                uptimeStringBuilder.append(minutes).append("m ");
            }
            uptimeStringBuilder.append(seconds).append("s ");

            embedBuilder.addField("Uptime", uptimeStringBuilder.toString(), true);

            // issue tracker field
            embedBuilder.addField("Support",
                    "[Issue tracker](https://git.beatrice.wtf/mind-overflow/HidekoBot)",
                    true);
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();


    }
}

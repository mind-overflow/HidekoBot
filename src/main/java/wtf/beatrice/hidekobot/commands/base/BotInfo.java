package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.util.FormatUtil;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;

public class BotInfo
{
    public static MessageEmbed generateEmbed(List<String> commandLabels)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle(Cache.getBotName());

        // thumbnail
        String botAvatarUrl = HidekoBot.getAPI().getSelfUser().getAvatarUrl();
        if(botAvatarUrl != null) embedBuilder.setThumbnail(botAvatarUrl);

        // help field
        long ownerId = Cache.getBotOwnerId();
        embedBuilder.addField("Getting started",
                "This instance is run by <@" + ownerId + ">.\nType `/help` for help! ",
                false);

        // type-specific commands list field
        StringBuilder commandsListBuilder = new StringBuilder();
        commandsListBuilder.append(commandLabels.size()).append( " total - ");
        for(int i = 0; i < commandLabels.size(); i++)
        {
            commandsListBuilder.append("`").append(commandLabels.get(i)).append("`");

            if(i + 1 != commandLabels.size()) // don't add comma in last iteration
            {
                commandsListBuilder.append(", ");
            }

        }
        embedBuilder.addField("Type commands", commandsListBuilder.toString(), false);

        // keep track of how many total commands we have
        int commandsCount = 0;

        // message commands info fields
        StringBuilder messageCommandsInfoBuilder = new StringBuilder();
        if(Cache.getMessageCommandListener() == null)
            messageCommandsInfoBuilder.append("❌ disabled");
        else {
            messageCommandsInfoBuilder.append("✅ available");
            commandsCount += Cache.getMessageCommandListener().getRegisteredCommands().size();
        }
        embedBuilder.addField("Message commands", messageCommandsInfoBuilder.toString(), true);

        // slash commands info fields
        StringBuilder slashCommandsInfoBuilder = new StringBuilder();
        if(Cache.getMessageCommandListener() == null)
            slashCommandsInfoBuilder.append("❌ disabled");
        else {
            slashCommandsInfoBuilder.append("✅ available");
            commandsCount += Cache.getSlashCommandListener().getRegisteredCommands().size();
        }
        embedBuilder.addField("Slash commands", slashCommandsInfoBuilder.toString(), true);

        // commands count fields
        embedBuilder.addField("Total commands", "Loaded: `" + commandsCount + "`", true);

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

        return embedBuilder.build();
    }
}

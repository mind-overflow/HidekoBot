package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.util.FormatUtil;
import wtf.beatrice.hidekobot.util.RandomUtil;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;

public class BotInfo
{
    private BotInfo()
    {
        throw new IllegalStateException("Utility class");
    }

    public static MessageEmbed generateEmbed(List<String> commandLabels)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle(Cache.getBotName());

        // thumbnail
        String botAvatarUrl = HidekoBot.getAPI().getSelfUser().getAvatarUrl();
        if (botAvatarUrl != null) embedBuilder.setThumbnail(botAvatarUrl);

        // help field
        long ownerId = Cache.getBotOwnerId();
        String prefix = Cache.getBotPrefix();
        embedBuilder.addField("Getting started",
                "This instance is run by <@" + ownerId + ">.\n" +
                        "Type `/help` for help! The bot prefix is `" + prefix + "`.",
                false);

        // type-specific commands list field
        StringBuilder commandsListBuilder = new StringBuilder();
        commandsListBuilder.append(commandLabels.size()).append(" total - ");
        for (int i = 0; i < commandLabels.size(); i++)
        {
            commandsListBuilder.append("`").append(commandLabels.get(i)).append("`");

            if (i + 1 != commandLabels.size()) // don't add comma in last iteration
            {
                commandsListBuilder.append(", ");
            }

        }
        embedBuilder.addField("Type commands", commandsListBuilder.toString(), false);

        // keep track of how many total commands we have
        int commandsCount = 0;

        // message commands info field
        String messageCommandsInfo;
        if (Cache.getMessageCommandListener() == null)
            messageCommandsInfo = "❌ disabled";
        else
        {
            messageCommandsInfo = "✅ available";
            commandsCount += Cache.getMessageCommandListener().getRegisteredCommands().size();
        }
        embedBuilder.addField("Message commands", messageCommandsInfo, true);

        // slash commands info field
        String slashCommandsInfo;
        if (Cache.getMessageCommandListener() == null)
            slashCommandsInfo = "❌ disabled";
        else
        {
            slashCommandsInfo = "✅ available";
            commandsCount += Cache.getSlashCommandListener().getRegisteredCommands().size();
        }
        embedBuilder.addField("Slash commands", slashCommandsInfo, true);

        // random.org integration field
        String randomOrgInfo;
        if (RandomUtil.isRandomOrgKeyValid())
        {
            randomOrgInfo = "✅ connected";
        } else
        {
            randomOrgInfo = "❌ disabled";
        }
        embedBuilder.addField("Random.org", randomOrgInfo, true);

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
        embedBuilder.addField("Uptime", FormatUtil.getNiceTimeDiff(Cache.getStartupTime()), true);

        // issue tracker field

        String link = "[Issue tracker](" + Cache.getRepositoryUrl() + "issues)";
        embedBuilder.addField("Support",
                link, true);

        // bot birthday field
        embedBuilder.addField("Bot age",
                Cache.getBotName() + " was created " + FormatUtil.getNiceTimeDiff(Cache.getBotBirthDate()) + "ago!",
                false);

        return embedBuilder.build();
    }
}

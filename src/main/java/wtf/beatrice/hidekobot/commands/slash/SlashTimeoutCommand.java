package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.commands.base.UserPunishment;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class SlashTimeoutCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash("timeout", "Timeout someone in the guild.")
                .addOption(OptionType.MENTIONABLE, "target",
                        "The member user to time out.",
                        true,
                        false)
                .addOption(OptionType.STRING, "duration",
                        "The duration of the timeout.",
                        true,
                        false)
                .addOption(OptionType.STRING, "reason",
                        "The reason for the punishment.",
                        false,
                        false)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        UserPunishment.handle(event, UserPunishment.PunishmentType.TIMEOUT);
    }
}

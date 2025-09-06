package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.commands.base.UserPunishment;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

@Component
public class SlashKickCommand extends SlashCommandImpl
{

    private final UserPunishment userPunishment;

    public SlashKickCommand(@Autowired UserPunishment userPunishment)
    {
        this.userPunishment = userPunishment;
    }

    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash("kick", "Kick someone from the guild.")
                .addOption(OptionType.MENTIONABLE, "target",
                        "The member user to kick.",
                        true,
                        false)
                .addOption(OptionType.STRING, "reason",
                        "The reason for the punishment.",
                        false,
                        false)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS));
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        userPunishment.handle(event, UserPunishment.PunishmentType.KICK);
    }
}

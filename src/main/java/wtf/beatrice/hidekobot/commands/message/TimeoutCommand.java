package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.commands.base.UserPunishment;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TimeoutCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("timeout"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return new ArrayList<Permission>(Collections.singletonList(Permission.MODERATE_MEMBERS));
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Timeout the mentioned user.";
    }

    @Nullable
    @Override
    public String getUsage() {
        return "<mentioned user> <duration> [reason]";
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        UserPunishment.handle(event, args, UserPunishment.PunishmentType.TIMEOUT);
    }
}

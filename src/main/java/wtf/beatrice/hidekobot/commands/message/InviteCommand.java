package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.commands.base.Invite;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InviteCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("invite"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null;
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get the bot's invite link.";
    }

    @Nullable
    @Override
    public String getUsage() {
        return null;
    }

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {


        MessageEmbed inviteEmbed = Invite.generateEmbed();
        Button inviteButton = Invite.getInviteButton();

        // if this is a guild, don't spam the invite in public but DM it
        if(event.getChannelType().isGuild())
        {
            event.getAuthor().openPrivateChannel().queue(privateChannel ->
            {
                privateChannel.sendMessageEmbeds(inviteEmbed)
                        .addActionRow(inviteButton)
                        .queue();
                event.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();
            },  (error) -> {
                event.getMessage().addReaction(Emoji.fromUnicode("❌")).queue();
            });
        } else {
            event.getMessage()
                    .replyEmbeds(inviteEmbed)
                    .addActionRow(inviteButton)
                    .queue();
        }

    }
}

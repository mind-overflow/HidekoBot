package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.commands.base.ProfileImage;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MessageAvatarCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels()
    {
        return new LinkedList<>(Collections.singletonList("avatar"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions()
    {
        return null; // anyone can use it
    }

    @Override
    public boolean passRawArgs()
    {
        return false;
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Get someone's avatar, or your own. You can additionally specify a resolution.";
    }

    @Nullable
    @Override
    public String getUsage()
    {
        return "[mentioned user] [resolution]";
    }

    @NotNull
    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.TOOLS;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        User user;
        int resolution = -1;

        // we have no specific order for user and resolution, so let's try parsing any arg as resolution
        // (mentions are handled differently by a specific method)
        boolean resFound = false;

        for (String arg : args)
        {
            try
            {
                int givenRes = Integer.parseInt(arg);
                resolution = ProfileImage.parseResolution(givenRes);
                resFound = true;
                break;
            } catch (NumberFormatException ignored)
            {
                // ignored because we're running a check after this block
            }
        }

        // fallback in case we didn't find any specified resolution
        if (!resFound) resolution = ProfileImage.parseResolution(512);

        // check if someone is mentioned
        Mentions mentions = event.getMessage().getMentions();
        if (mentions.getMentions().isEmpty())
        {
            user = event.getAuthor();
        } else
        {
            String mentionedId = mentions.getMentions().get(0).getId();
            user = HidekoBot.getAPI().retrieveUserById(mentionedId).complete();
        }

        // in case of issues, fallback to the sender
        if (user == null) user = event.getAuthor();

        // send a response
        MessageResponse response = ProfileImage.buildResponse(resolution, user, ProfileImage.ImageType.AVATAR);
        if (response.content() != null)
        {
            event.getMessage().reply(response.content()).queue();
        } else if (response.embed() != null)
        {
            event.getMessage().replyEmbeds(response.embed()).queue();
        }
    }
}

package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.commands.base.Avatar;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AvatarCommand  implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("avatar"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null; // anyone can use it
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        int[] acceptedSizes = Cache.getSupportedAvatarResolutions();

        User user = null;
        int resolution = -1;

        // we have no specific order for user and resolution, so let's try parsing any arg as resolution
        // (mentions are handled differently by a specific method)
        boolean resFound = false;

        for (String arg : args) {
            try {
                int givenRes = Integer.parseInt(arg);
                resolution = Avatar.parseResolution(givenRes);
                resFound = true;
                break;
            } catch (NumberFormatException ignored) {
            }
        }

        // fallback in case we didn't find any specified resolution
        if(!resFound) resolution = Avatar.parseResolution(512);

        // check if someone is mentioned
        Mentions mentions = event.getMessage().getMentions();
        if(mentions.getMentions().isEmpty())
        {
            user = event.getAuthor();
        } else
        {
            String mentionedId = mentions.getMentions().get(0).getId();
            user = HidekoBot.getAPI().retrieveUserById(mentionedId).complete();
        }

        // in case of issues, fallback to the sender
        if(user == null) user = event.getAuthor();

        // send a response
        MessageEmbed embed = Avatar.buildEmbed(resolution, user);
        event.getMessage().replyEmbeds(embed).queue();
    }
}

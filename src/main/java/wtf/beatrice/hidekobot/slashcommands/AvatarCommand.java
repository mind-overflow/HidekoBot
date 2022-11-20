package wtf.beatrice.hidekobot.slashcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AvatarCommand
{
    // discord api returns a broken image if you don't use specific sizes (powers of 2), so we limit it to these
    private final int[] acceptedSizes = { 16, 32, 64, 128, 256, 512, 1024 };

    public AvatarCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        User user;
        int resolution;

        OptionMapping userArg = event.getOption("user");
        if(userArg != null)
        {
            user = userArg.getAsUser();
        } else {
            user = event.getUser();
        }

        OptionMapping sizeArg = event.getOption("size");
        if(sizeArg != null)
        {
            resolution = sizeArg.getAsInt();

            // method to find closest value to accepted values
            int distance = Math.abs(acceptedSizes[0] - resolution);
            int idx = 0;
            for(int c = 1; c < acceptedSizes.length; c++){
                int cdistance = Math.abs(acceptedSizes[c] - resolution);
                if(cdistance < distance){
                    idx = c;
                    distance = cdistance;
                }
            }
            resolution = acceptedSizes[idx];

        } else {
            resolution = 512;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        {
            embedBuilder.setColor(Color.PINK);
            embedBuilder.setAuthor(event.getUser().getAsTag(), null, event.getUser().getEffectiveAvatarUrl());
            embedBuilder.setTitle(user.getAsTag() + "'s profile picture");

            embedBuilder.addField("Current resolution", resolution + " × " + resolution, false);

            StringBuilder links = new StringBuilder();
            for(int pos = 0; pos < acceptedSizes.length; pos++)
            {
                int currSize = acceptedSizes[pos];

                String currLink = user.getEffectiveAvatar().getUrl(currSize);

                links.append("[").append(currSize).append("px](").append(currLink).append(")");
                if(pos+1 != acceptedSizes.length)
                {
                    links.append(" | ");
                }
            }

            embedBuilder.addField("Available resolutions", links.toString(), false);



            embedBuilder.setImage(user.getEffectiveAvatar().getUrl(resolution));
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
}

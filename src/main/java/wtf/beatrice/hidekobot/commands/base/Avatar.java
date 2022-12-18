package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import wtf.beatrice.hidekobot.Cache;

public class Avatar
{
    public static int parseResolution(int resolution)
    {
        int[] acceptedSizes = Cache.getSupportedAvatarResolutions();

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

        return acceptedSizes[idx];
    }

    public static MessageEmbed buildEmbed(int resolution, User user)
    {
        int[] acceptedSizes = Cache.getSupportedAvatarResolutions();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("Profile picture");

        embedBuilder.addField("User", "<@" + user.getId() + ">", false);

        embedBuilder.addField("Current resolution", resolution + " × " + resolution, false);

        // string builder to create a string that links to all available resolutions
        StringBuilder links = new StringBuilder();
        for(int pos = 0; pos < acceptedSizes.length; pos++)
        {
            int currSize = acceptedSizes[pos];

            String currLink = user.getEffectiveAvatar().getUrl(currSize);

            links.append("[").append(currSize).append("px](").append(currLink).append(")");
            if(pos + 1 != acceptedSizes.length) // don't add a separator on the last iteration
            {
                links.append(" | ");
            }
        }

        embedBuilder.addField("Available resolutions", links.toString(), false);



        embedBuilder.setImage(user.getEffectiveAvatar().getUrl(resolution));
        return embedBuilder.build();
    }
}

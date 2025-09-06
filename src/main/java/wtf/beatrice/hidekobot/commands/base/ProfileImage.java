package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.MessageResponse;

@Component
public class ProfileImage
{

    public int parseResolution(int resolution)
    {
        int[] acceptedSizes = Cache.getSupportedAvatarResolutions();

        // method to find closest value to accepted values
        int distance = Math.abs(acceptedSizes[0] - resolution);
        int idx = 0;
        for (int c = 1; c < acceptedSizes.length; c++)
        {
            int cdistance = Math.abs(acceptedSizes[c] - resolution);
            if (cdistance < distance)
            {
                idx = c;
                distance = cdistance;
            }
        }

        return acceptedSizes[idx];
    }

    public MessageResponse buildResponse(int resolution, User user, ImageType imageType)
    {
        String imageTypeName = imageType.name().toLowerCase();
        String resolutionString;
        String imageLink = null;

        User.Profile userProfile = user.retrieveProfile().complete();
        ImageProxy bannerProxy = userProfile.getBanner();

        if (imageType == ImageType.AVATAR)
        {
            resolutionString = resolution + " × " + resolution;
            imageLink = user.getEffectiveAvatar().getUrl(resolution);
        } else
        {
            int verticalRes = 361 * resolution / 1024;
            resolutionString = resolution + " × " + verticalRes;
            if (bannerProxy != null)
                imageLink = bannerProxy.getUrl(resolution);
        }

        int[] acceptedSizes = Cache.getSupportedAvatarResolutions();
        EmbedBuilder embedBuilder = new EmbedBuilder();


        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("Profile " + imageTypeName);

        embedBuilder.addField("User", user.getAsMention(), false);

        embedBuilder.addField("Current resolution", resolutionString, false);

        // string builder to create a string that links to all available resolutions
        StringBuilder links = new StringBuilder();
        for (int pos = 0; pos < acceptedSizes.length; pos++)
        {
            int currSize = acceptedSizes[pos];

            String currLink;
            if (imageType == ImageType.AVATAR)
            {
                currLink = user.getEffectiveAvatar().getUrl(currSize);
            } else
            {
                if (bannerProxy == null) break;
                currLink = bannerProxy.getUrl(currSize);
            }

            links.append("**[").append(currSize).append("px](").append(currLink).append(")**");
            if (pos + 1 != acceptedSizes.length) // don't add a separator on the last iteration
            {
                links.append(" | ");
            }
        }

        embedBuilder.addField("Available resolutions", links.toString(), false);


        if (imageLink != null)
            embedBuilder.setImage(imageLink);


        if (imageLink == null)
        {
            String error = "I couldn't find " + user.getAsMention() + "'s " + imageTypeName + "!";
            return new MessageResponse(error, null);
        } else
        {
            return new MessageResponse(null, embedBuilder.build());
        }
    }

    public enum ImageType
    {
        AVATAR, BANNER;
    }
}

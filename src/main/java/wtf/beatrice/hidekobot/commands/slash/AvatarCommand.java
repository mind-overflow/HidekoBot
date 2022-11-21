package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;

public class AvatarCommand
{

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because this might take a moment
        event.deferReply().queue();

        User user;
        int resolution;

        int[] acceptedSizes = Cache.getSupportedAvatarResolutions();


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

        // embed processing
        {
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
        }

        event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
}

package wtf.beatrice.hidekobot.slashcommands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class AvatarCommand
{
    // discord api returns a broken image if you don't use specific sizes (powers of 2), so we limit it to these
    private final int[] acceptedSizes = { 16, 32, 64, 128, 256, 512, 1024 };

    public AvatarCommand(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        User user;
        int size;

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
            size = sizeArg.getAsInt();

            // method to find closest value to accepted values
            int distance = Math.abs(acceptedSizes[0] - size);
            int idx = 0;
            for(int c = 1; c < acceptedSizes.length; c++){
                int cdistance = Math.abs(acceptedSizes[c] - size);
                if(cdistance < distance){
                    idx = c;
                    distance = cdistance;
                }
            }
            size = acceptedSizes[idx];

        } else {
            size = 512;
        }

        event.getHook().sendMessage(user.getEffectiveAvatar().getUrl(size)).queue();
    }
}

package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.Avatar;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

public class AvatarCommand extends SlashCommandImpl
{
    @Override
    public CommandData getSlashCommandData() {
        return Commands.slash("avatar", "Get someone's profile picture.")
                .addOption(OptionType.USER, "user", "User you want to grab the avatar of.")
                .addOption(OptionType.INTEGER, "size", "The size of the returned image.",
                        false,
                        true);
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        // defer reply because this might take a moment
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
            resolution = Avatar.parseResolution(sizeArg.getAsInt());
        } else {
            resolution = Avatar.parseResolution(512);
        }

        MessageEmbed embed = Avatar.buildEmbed(resolution, user);
        event.getHook().editOriginalEmbeds(embed).queue();
    }
}

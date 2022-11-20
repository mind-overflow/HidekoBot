package wtf.beatrice.hidekobot.slashcommands;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;

public class InviteCommand
{
    public InviteCommand(@NotNull SlashCommandInteractionEvent event)
    {
        ReplyCallbackAction reply = event.reply("Here's your link ✨ " + Configuration.getInviteUrl());

        // only make message permanent in DMs
        if(!(event.getChannelType() == ChannelType.PRIVATE))
        {
            reply = reply.setEphemeral(true);
        }

        reply.queue();
    }
}

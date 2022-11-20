package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.commands.slash.*;

public class SlashCommandListener extends ListenerAdapter
{

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        switch (event.getName().toLowerCase()) {
            case "avatar" -> new AvatarCommand(event);
            case "clear" -> new ClearChatCommand(event);
            case "coinflip" -> new CoinFlipCommand(event);
            case "die" -> new DieCommand(event);
            case "invite" -> new InviteCommand(event);
            case "ping" -> new PingCommand(event);
        }
    }
}

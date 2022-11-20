package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.slashcommands.ClearChatCommand;
import wtf.beatrice.hidekobot.slashcommands.CoinFlipCommand;
import wtf.beatrice.hidekobot.slashcommands.DieCommand;
import wtf.beatrice.hidekobot.slashcommands.PingCommand;

public class SlashCommandListener extends ListenerAdapter
{

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        switch (event.getName().toLowerCase()) {
            case "ping" -> new PingCommand(event);
            case "die" -> new DieCommand(event);
            case "coinflip" -> new CoinFlipCommand(event);
            case "clear" -> new ClearChatCommand(event);
        }
    }
}

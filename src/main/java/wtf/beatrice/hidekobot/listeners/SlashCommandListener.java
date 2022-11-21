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
            case "avatar" -> new AvatarCommand().runSlashCommand(event);
            case "botinfo" -> new BotInfoCommand().runSlashCommand(event);
            case "clear" -> new ClearCommand().runSlashCommand(event);
            case "coinflip" -> new CoinFlipCommand().runSlashCommand(event);
            case "die" -> new DieCommand().runSlashCommand(event);
            case "help" -> new HelpCommand().runSlashCommand(event);
            case "invite" -> new InviteCommand().runSlashCommand(event);
            case "ping" -> new PingCommand().runSlashCommand(event);
            case "say" -> new SayCommand().runSlashCommand(event);
        }
    }
}

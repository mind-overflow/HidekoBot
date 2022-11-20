package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.slashcommands.ClearChatCommand;
import wtf.beatrice.hidekobot.slashcommands.CoinFlipCommand;
import wtf.beatrice.hidekobot.slashcommands.DieCommand;
import wtf.beatrice.hidekobot.slashcommands.PingCommand;
import wtf.beatrice.hidekobot.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

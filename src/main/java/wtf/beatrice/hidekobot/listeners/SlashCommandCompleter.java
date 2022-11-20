package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wtf.beatrice.hidekobot.commands.completer.AvatarCompleter;

public class SlashCommandCompleter extends ListenerAdapter
{

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
    {
        switch (event.getName().toLowerCase()) {
            case "avatar" -> new AvatarCompleter(event);
        }
    }
}

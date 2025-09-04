package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wtf.beatrice.hidekobot.objects.commands.SlashArgumentsCompleter;

import java.util.LinkedList;
import java.util.TreeMap;

public class SlashCommandCompletionListener extends ListenerAdapter
{

    // map that stores command label and command auto-completer alphabetically.
    private final TreeMap<String, SlashArgumentsCompleter> registeredCompleters = new TreeMap<>();

    public void registerCommandCompleter(SlashArgumentsCompleter completer)
    {
        String parentCommandName = completer.getCommand().getCommandName();
        registeredCompleters.remove(parentCommandName);
        registeredCompleters.put(parentCommandName, completer);
    }

    public SlashArgumentsCompleter getRegisteredCompleter(String label)
    {
        return registeredCompleters.get(label);
    }

    public LinkedList<SlashArgumentsCompleter> getRegisteredCompleters()
    {
        return new LinkedList<>(registeredCompleters.values());
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
    {
        String commandName = event.getName().toLowerCase();
        SlashArgumentsCompleter completer = registeredCompleters.get(commandName);
        if (completer == null) return;

        // not running in a thread because nothing heavy should be done here...
        completer.runCompletion(event);
    }
}

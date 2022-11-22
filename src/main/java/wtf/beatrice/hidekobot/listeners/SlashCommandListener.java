package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.objects.SlashCommand;

import java.util.LinkedList;
import java.util.TreeMap;

public class SlashCommandListener extends ListenerAdapter
{

    // map storing command label and command object alphabetically.
    private final TreeMap<String, SlashCommand> registeredCommands = new TreeMap<>();

    public void registerCommand(SlashCommand command)
    {
        registeredCommands.remove(command.getCommandName());
        registeredCommands.put(command.getCommandName(), command);
    }

    public SlashCommand getRegisteredCommand(String label)
    { return registeredCommands.get(label); }

    public LinkedList<SlashCommand> getRegisteredCommands()
    { return new LinkedList<>(registeredCommands.values()); }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        String commandName = event.getName().toLowerCase();
        SlashCommand command = registeredCommands.get(commandName);
        if(command == null) return;

        command.runSlashCommand(event);
    }
}

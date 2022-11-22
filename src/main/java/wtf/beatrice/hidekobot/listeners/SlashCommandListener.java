package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.objects.SlashCommand;

import java.util.HashMap;

public class SlashCommandListener extends ListenerAdapter
{

    HashMap<String, SlashCommand> registeredCommands = new HashMap<>();

    public void registerCommand(SlashCommand command)
    {
        registeredCommands.remove(command.getCommandName());
        registeredCommands.put(command.getCommandName(), command);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        String commandName = event.getName().toLowerCase();
        SlashCommand command = registeredCommands.get(commandName);
        if(command == null) return;

        command.runSlashCommand(event);
    }
}

package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import wtf.beatrice.hidekobot.slashcommands.AvatarCommand;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandCompleter extends ListenerAdapter
{

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getName().equals("avatar"))
        {
            if(event.getFocusedOption().getName().equals("size"))
            {

                List<Command.Choice> options = new ArrayList<>();

                for(int res : AvatarCommand.acceptedSizes)
                {
                    String resString = String.valueOf(res);
                    String userInput = event.getFocusedOption().getValue();

                    if(resString.startsWith(userInput))
                        options.add(new Command.Choice(resString, res));
                }

                event.replyChoices(options).queue();
            }
        }
    }
}

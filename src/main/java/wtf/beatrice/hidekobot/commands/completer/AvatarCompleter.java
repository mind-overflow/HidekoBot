package wtf.beatrice.hidekobot.commands.completer;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Configuration;

import java.util.ArrayList;
import java.util.List;

public class AvatarCompleter
{

    public AvatarCompleter(@NotNull CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("size"))
        {

            List<Command.Choice> options = new ArrayList<>();

            for(int res : Configuration.getSupportedAvatarResolutions())
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

package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wtf.beatrice.hidekobot.commands.base.Trivia;

public class SelectMenuInteractionListener extends ListenerAdapter
{

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event)
    {
        switch (event.getComponentId().toLowerCase()) {

            // trivia
            case "trivia_categories" -> Trivia.handleMenuSelection(event);
        }
    }
}

package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.commands.base.Trivia;

public class SelectMenuInteractionListener extends ListenerAdapter
{

    private static final Logger LOGGER = LoggerFactory.getLogger(SelectMenuInteractionListener.class);

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event)
    {
        switch (event.getComponentId().toLowerCase())
        {

            // trivia
            case "trivia_categories" -> Trivia.handleMenuSelection(event);

            // error handling
            default -> LOGGER.warn("Received unhandled {}", event.getClass().getSimpleName());
        }
    }
}

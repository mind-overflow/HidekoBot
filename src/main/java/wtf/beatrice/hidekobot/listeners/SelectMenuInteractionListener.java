package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.util.TriviaUtil;

public class SelectMenuInteractionListener extends ListenerAdapter
{

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event)
    {
        switch (event.getComponentId().toLowerCase()) {

            // trivia
            case "trivia_categories" -> TriviaUtil.handleMenuSelection(event);
        }
    }
}

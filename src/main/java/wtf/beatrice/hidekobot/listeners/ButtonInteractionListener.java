package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.commands.base.UrbanDictionary;
import wtf.beatrice.hidekobot.util.CommandUtil;

public class ButtonInteractionListener extends ListenerAdapter
{

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {

        switch (event.getComponentId().toLowerCase()) {

            // coinflip
            case "coinflip_reflip" -> CoinFlip.buttonReFlip(event);

            // generic dismiss button
            case "generic_dismiss" -> CommandUtil.delete(event);

            // urban dictionary navigation
            case "urban_nextpage" -> UrbanDictionary.changePage(event, UrbanDictionary.ChangeType.NEXT);
            case "urban_previouspage" -> UrbanDictionary.changePage(event, UrbanDictionary.ChangeType.PREVIOUS);

        }

    }

}

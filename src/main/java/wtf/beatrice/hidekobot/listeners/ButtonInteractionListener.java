package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wtf.beatrice.hidekobot.commands.base.ClearChat;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.commands.base.UrbanDictionary;
import wtf.beatrice.hidekobot.commands.message.UrbanDictionaryCommand;

public class ButtonInteractionListener extends ListenerAdapter
{

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {

        switch (event.getComponentId().toLowerCase()) {

            // coinflip
            case "coinflip_reflip" -> CoinFlip.buttonReFlip(event);

            // clearchat command
            case "clear_dismiss" -> ClearChat.dismissMessage(event);

            // urban dictionary navigation
            case "urban_nextpage" -> UrbanDictionary.changePage(event, true);
            case "urban_previouspage" -> UrbanDictionary.changePage(event, false);
            case "urban_delete" -> UrbanDictionary.delete(event);

        }

    }

}

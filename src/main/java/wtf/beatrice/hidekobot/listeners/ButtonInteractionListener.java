package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wtf.beatrice.hidekobot.commands.base.ClearChat;
import wtf.beatrice.hidekobot.commands.slash.CoinFlipCommand;

public class ButtonInteractionListener extends ListenerAdapter
{

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {

        switch (event.getComponentId().toLowerCase()) {

            // coinflip
            case "coinflip_reflip" -> new CoinFlipCommand().buttonReFlip(event);

            // clearchat command
            case "clear_dismiss" -> ClearChat.dismissMessage(event);

        }

    }

}

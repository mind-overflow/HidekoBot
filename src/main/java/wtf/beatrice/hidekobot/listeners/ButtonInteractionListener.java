package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.commands.base.UrbanDictionary;
import wtf.beatrice.hidekobot.util.CommandUtil;
import wtf.beatrice.hidekobot.util.TriviaUtil;

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

            // trivia
            case "trivia_correct" -> TriviaUtil.handleAnswer(event, TriviaUtil.AnswerType.CORRECT);
            case "trivia_wrong_1", "trivia_wrong_2", "trivia_wrong_3" ->
                    TriviaUtil.handleAnswer(event, TriviaUtil.AnswerType.WRONG);

        }

    }

}

package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.commands.base.Trivia;
import wtf.beatrice.hidekobot.commands.base.UrbanDictionary;
import wtf.beatrice.hidekobot.util.CommandUtil;

@Component
public class ButtonInteractionListener extends ListenerAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ButtonInteractionListener.class);

    private final CommandUtil commandUtil;

    public ButtonInteractionListener(CommandUtil commandUtil)
    {
        this.commandUtil = commandUtil;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {

        switch (event.getComponentId().toLowerCase())
        {

            // coinflip
            case "coinflip_reflip" -> CoinFlip.buttonReFlip(event);

            // generic dismiss button
            case "generic_dismiss" -> commandUtil.delete(event);

            // urban dictionary navigation
            case "urban_nextpage" -> UrbanDictionary.changePage(event, UrbanDictionary.ChangeType.NEXT);
            case "urban_previouspage" -> UrbanDictionary.changePage(event, UrbanDictionary.ChangeType.PREVIOUS);

            // trivia
            case "trivia_correct" -> Trivia.handleAnswer(event, Trivia.AnswerType.CORRECT);
            case "trivia_wrong_1", "trivia_wrong_2", "trivia_wrong_3" ->
                    Trivia.handleAnswer(event, Trivia.AnswerType.WRONG);

            // error handling
            default -> LOGGER.warn("Received unhandled {}", event.getClass().getSimpleName());


        }

    }

}

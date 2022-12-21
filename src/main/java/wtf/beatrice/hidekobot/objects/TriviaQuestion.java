package wtf.beatrice.hidekobot.objects;

import java.util.List;

public record TriviaQuestion(String question, String correctAnswer,
                             List<String> wrongAnswers) {

}

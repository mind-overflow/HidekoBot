package wtf.beatrice.hidekobot.objects.comparators;

import wtf.beatrice.hidekobot.objects.fun.TriviaScore;

import java.util.Comparator;

/**
 * This class gets two trivia scores, and compares their score.
 */
public class TriviaScoreComparator implements Comparator<TriviaScore> {

    @Override
    public int compare(TriviaScore o1, TriviaScore o2) {
        return Integer.compare(o2.getScore(), o1.getScore()); // inverted, because higher number should come first
    }
}

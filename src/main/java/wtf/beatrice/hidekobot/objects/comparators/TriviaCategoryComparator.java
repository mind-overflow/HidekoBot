package wtf.beatrice.hidekobot.objects.comparators;

import wtf.beatrice.hidekobot.objects.fun.TriviaCategory;

import java.util.Comparator;

/**
 * This class gets two trivia categories, and compares them by their name.
 */
public class TriviaCategoryComparator implements Comparator<TriviaCategory> {

    @Override
    public int compare(TriviaCategory o1, TriviaCategory o2) {
        return CharSequence.compare(o1.categoryName(), o2.categoryName());
    }
}

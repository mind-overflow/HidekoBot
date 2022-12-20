package wtf.beatrice.hidekobot.util;

import wtf.beatrice.hidekobot.Cache;

import java.util.Random;

public class RandomUtil
{
    /**
     * Returns a random integer picked in a range.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @return a random number in range [min; max]
     */
    public static int getRandomNumber(int min, int max)
    {
        if(min == max) return min; // dumbass
        if(min > max) // swap em
        {
            min = min - max;
            max = min + max;
            min = max - min;
        }


        // find our range of randomness (eg. 5 -> 8 = 4), add +1 since we want to be inclusive at both sides
        int difference = (max - min) + 1;

        // find a number between 0 and our range (eg. 5 -> 8 = 0 -> 3)
        int randomTemp = Cache.getRandom().nextInt(difference);

        // add the minimum value, so we are sure to be in the original range (0->5, 1->6, 2->7, 3->8)
        return randomTemp + min;
    }
}

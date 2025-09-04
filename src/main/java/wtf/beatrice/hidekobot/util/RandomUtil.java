package wtf.beatrice.hidekobot.util;

import org.random.util.RandomOrgRandom;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.datasources.ConfigurationEntry;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil
{

    private RandomUtil()
    {
        throw new IllegalStateException("Utility class");
    }

    // the Random instance that we should always use when looking for an RNG based thing.
    // the seed is updated periodically, if the random.org integration is enabled.
    private static Random randomInstance = new SecureRandom();

    /**
     * Returns a random integer picked in a range.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @return a random number in range [min; max]
     */
    public static int getRandomNumber(int min, int max)
    {
        if (min == max) return min; // dumbass
        if (min > max) // swap em
        {
            min = min - max;
            max = min + max;
            min = max - min;
        }


        // find our range of randomness (eg. 5 -> 8 = 4), add +1 since we want to be inclusive at both sides
        int difference = (max - min) + 1;

        // find a number between 0 and our range (eg. 5 -> 8 = 0 -> 3)
        int randomTemp = getRandom().nextInt(difference);

        // add the minimum value, so we are sure to be in the original range (0->5, 1->6, 2->7, 3->8)
        return randomTemp + min;
    }


    public static Random getRandom()
    {
        return randomInstance;
    }

    public static void initRandomOrg()
    {
        /* we use the random.org instance to generate 160 random bytes.
        then, we're feeding those 160 bytes as a seed for a SecureRandom.

        this is preferred to calling the RandomOrgRandom directly every time,
        because it has to query the api and (1) takes a long time, especially with big
        dice rolls, and (2) you'd run in the limits extremely quickly if the bot
        was run publicly for everyone to use.
         */
        String apiKey = Cache.getRandomOrgApiKey();

        RandomOrgRandom randomOrg = new RandomOrgRandom(apiKey);
        byte[] randomBytes = new byte[160];
        randomOrg.nextBytes(randomBytes);

        randomInstance = new SecureRandom(randomBytes);
    }


    public static boolean isRandomOrgKeyValid()
    {
        String apiKey = Cache.getRandomOrgApiKey();

        return apiKey != null &&
                !apiKey.isEmpty() &&
                !apiKey.equals(ConfigurationEntry.RANDOM_ORG_API_KEY.getDefaultValue());
    }
}

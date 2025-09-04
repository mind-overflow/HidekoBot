package wtf.beatrice.hidekobot.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.RandomUtil;

/**
 * This runnable pulls a random seed from random.org and used it to feed a SecureRandom,
 * if the integration is enabled.
 * <br/>
 * This is necessary since we are not directly accessing random.org for each random number we
 * need, and thus, only the seed is random - not the algorithm applied to it to compute the numbers.
 */
public class RandomOrgSeedTask implements Runnable
{

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomOrgSeedTask.class);

    @Override
    public void run()
    {
        if (RandomUtil.isRandomOrgKeyValid())
        {
            if (Cache.isVerbose()) LOGGER.info("Updating Random seed from random.org...");
            RandomUtil.initRandomOrg();
            if (Cache.isVerbose()) LOGGER.info("Random.org seed updated!");
        }
    }
}

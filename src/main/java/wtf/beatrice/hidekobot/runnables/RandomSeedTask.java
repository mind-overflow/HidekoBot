package wtf.beatrice.hidekobot.runnables;

import wtf.beatrice.hidekobot.Cache;

public class RandomSeedTask implements Runnable
{

    @Override
    public void run() {
        Cache.setRandomSeed(System.currentTimeMillis());
    }
}

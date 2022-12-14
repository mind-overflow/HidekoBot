package wtf.beatrice.hidekobot.runnables;

import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeartBeatTask implements Runnable
{
    private final Logger logger;

    public HeartBeatTask()
    {
        logger = new Logger(getClass());
    }



    @Override
    public void run()
    {
        String urlString = Cache.getFullHeartBeatLink();
        if(urlString == null || urlString.isEmpty()) return;

        try {

            URL heartbeatUrl = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) heartbeatUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if(200 <= responseCode && responseCode < 300)
            {
                // only log ok response codes when verbosity is enabled
                if(Cache.isVerbose()) logger.log("Heartbeat response code: " + responseCode);
            }
            else
            {
                logger.log("Heartbeat returned problematic response code: " + responseCode);
            }

        } catch (IOException e) {
            logger.log("Error while trying to push heartbeat: " + e.getMessage());
        }

    }
}

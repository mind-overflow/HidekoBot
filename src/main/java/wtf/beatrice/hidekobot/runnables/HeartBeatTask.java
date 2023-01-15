package wtf.beatrice.hidekobot.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.Cache;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeartBeatTask implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatTask.class);

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
                if(Cache.isVerbose()) LOGGER.info("Heartbeat response code: {}", responseCode);
            }
            else
            {
                LOGGER.error("Heartbeat returned problematic response code: {}", responseCode);
            }

        } catch (IOException e) {
            LOGGER.error("Error while trying to push heartbeat", e);
        }

    }
}

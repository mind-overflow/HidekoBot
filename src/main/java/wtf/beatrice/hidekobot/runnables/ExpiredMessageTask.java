package wtf.beatrice.hidekobot.runnables;

import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;
import wtf.beatrice.hidekobot.util.CommandUtil;
import wtf.beatrice.hidekobot.util.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpiredMessageTask implements Runnable {

    private final DateTimeFormatter formatter;
    private final Logger logger;
    private DatabaseSource databaseSource;


    public ExpiredMessageTask()
    {
        String format = Cache.getExpiryTimestampFormat();
        formatter = DateTimeFormatter.ofPattern(format);
        databaseSource = Cache.getDatabaseSource();
        logger = new Logger(getClass());
    }


    @Override
    public void run() {

        databaseSource = Cache.getDatabaseSource();
        if(databaseSource == null) return;

        List<String> expiringMessages = Cache.getDatabaseSource().getQueuedExpiringMessages();
        if(expiringMessages == null || expiringMessages.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for(String messageId : expiringMessages)
        {

            if(Cache.isVerbose()) logger.log("expired check: " + messageId);

            String expiryTimestamp = databaseSource.getQueuedExpiringMessageExpiryDate(messageId);
            if(expiryTimestamp == null || expiryTimestamp.equals("")) // if missing timestamp
            {
                // count it as already expired
                databaseSource.untrackExpiredMessage(messageId);
                // move on to next message
                continue;
            }


            LocalDateTime expiryDate = LocalDateTime.parse(expiryTimestamp, formatter);
            if(now.isAfter(expiryDate))
            {
                if(Cache.isVerbose()) logger.log("expired: " + messageId);
                CommandUtil.disableExpired(messageId);
            }
        }

    }
}

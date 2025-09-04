package wtf.beatrice.hidekobot.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;
import wtf.beatrice.hidekobot.util.CommandUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpiredMessageTask implements Runnable
{

    private final DateTimeFormatter formatter;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredMessageTask.class);
    private DatabaseSource databaseSource;


    public ExpiredMessageTask()
    {
        String format = Cache.getExpiryTimestampFormat();
        formatter = DateTimeFormatter.ofPattern(format);
        databaseSource = Cache.getDatabaseSource();
    }


    @Override
    public void run()
    {

        databaseSource = Cache.getDatabaseSource();
        if (databaseSource == null) return;

        List<String> expiringMessages = Cache.getDatabaseSource().getQueuedExpiringMessages();
        if (expiringMessages == null || expiringMessages.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for (String messageId : expiringMessages)
        {

            if (Cache.isVerbose()) LOGGER.info("expired check: {}", messageId);

            String expiryTimestamp = databaseSource.getQueuedExpiringMessageExpiryDate(messageId);
            if (expiryTimestamp == null || expiryTimestamp.isEmpty()) // if missing timestamp
            {
                // count it as already expired
                databaseSource.untrackExpiredMessage(messageId);
                // move on to next message
                continue;
            }


            LocalDateTime expiryDate = LocalDateTime.parse(expiryTimestamp, formatter);
            if (now.isAfter(expiryDate))
            {
                if (Cache.isVerbose()) LOGGER.info("expired: {}", messageId);
                CommandUtil.disableExpired(messageId);
            }
        }

    }
}

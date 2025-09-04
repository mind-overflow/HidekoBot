package wtf.beatrice.hidekobot.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.services.CommandService;
import wtf.beatrice.hidekobot.services.DatabaseService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpiredMessageTask implements Runnable
{

    private final DatabaseService databaseService;
    private final CommandService commandService;

    private final DateTimeFormatter formatter;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredMessageTask.class);


    public ExpiredMessageTask(DatabaseService databaseService,
                              CommandService commandService)
    {
        this.databaseService = databaseService;
        this.commandService = commandService;
        String format = Cache.getExpiryTimestampFormat();
        formatter = DateTimeFormatter.ofPattern(format);
    }


    @Override
    public void run()
    {

        List<String> expiringMessages = databaseService.getQueuedExpiringMessages();
        if (expiringMessages == null || expiringMessages.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for (String messageId : expiringMessages)
        {

            if (Cache.isVerbose()) LOGGER.info("expired check: {}", messageId);

            String expiryTimestamp = databaseService.getQueuedExpiringMessageExpiryDate(messageId);
            if (expiryTimestamp == null || expiryTimestamp.isEmpty()) // if missing timestamp
            {
                // count it as already expired
                databaseService.untrackExpiredMessage(messageId);
                // move on to next message
                continue;
            }


            LocalDateTime expiryDate = LocalDateTime.parse(expiryTimestamp, formatter);
            if (now.isAfter(expiryDate))
            {
                if (Cache.isVerbose()) LOGGER.info("expired: {}", messageId);
                commandService.disableExpired(messageId);
            }
        }

    }
}

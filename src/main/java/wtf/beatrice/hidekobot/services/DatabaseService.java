package wtf.beatrice.hidekobot.services;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.entities.CommandRunner;
import wtf.beatrice.hidekobot.entities.PendingDisabledMessage;
import wtf.beatrice.hidekobot.entities.UrbanDictionaryEntry;
import wtf.beatrice.hidekobot.repositories.CommandRunnerRepository;
import wtf.beatrice.hidekobot.repositories.PendingDisabledMessageRepository;
import wtf.beatrice.hidekobot.repositories.UrbanDictionaryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class DatabaseService
{

    private final PendingDisabledMessageRepository pendingRepo;
    private final CommandRunnerRepository runnerRepo;
    private final UrbanDictionaryRepository urbanRepo;

    public DatabaseService(PendingDisabledMessageRepository p, CommandRunnerRepository c, UrbanDictionaryRepository u)
    {
        this.pendingRepo = p;
        this.runnerRepo = c;
        this.urbanRepo = u;
    }

    // trackRanCommandReply
    public void trackRanCommandReply(Message message, User user)
    {
        String userId = user.getId();
        String guildId = message.getChannelType().isGuild() ? message.getGuild().getId() : userId;

        CommandRunner row = new CommandRunner();
        row.setMessageId(message.getId());
        row.setGuildId(guildId);
        row.setChannelId(message.getChannel().getId());
        row.setUserId(userId);
        row.setChannelType(message.getChannelType().name());

        runnerRepo.save(row);
    }

    public boolean isUserTrackedFor(String userId, String messageId)
    {
        return runnerRepo.findById(messageId)
                .map(r -> userId.equals(r.getUserId()))
                .orElse(false);
    }


    public ChannelType getTrackedMessageChannelType(String messageId)
    {
        return runnerRepo.findById(messageId)
                .map(r -> ChannelType.valueOf(r.getChannelType()))
                .orElse(null);
    }

    public String getTrackedReplyUserId(String messageId)
    {
        return runnerRepo.findById(messageId)
                .map(CommandRunner::getUserId)
                .orElse(null);
    }
    
    public void queueDisabling(Message message)
    {
        String guildId = message.getChannelType().isGuild() ? message.getGuild().getId() : "PRIVATE";

        LocalDateTime expiry = LocalDateTime.now().plusSeconds(Cache.getExpiryTimeSeconds());
        String formatted = DateTimeFormatter.ofPattern(Cache.getExpiryTimestampFormat()).format(expiry);

        PendingDisabledMessage row = new PendingDisabledMessage();
        row.setMessageId(message.getId());
        row.setChannelId(message.getChannel().getId());
        row.setGuildId(guildId);
        row.setExpiryTimestamp(formatted);

        pendingRepo.save(row);
    }

    public List<String> getQueuedExpiringMessages()
    {
        return pendingRepo.findAll()
                .stream()
                .map(PendingDisabledMessage::getMessageId)
                .toList();
    }

    public void untrackExpiredMessage(String messageId)
    {
        pendingRepo.deleteById(messageId);
        runnerRepo.deleteById(messageId);
        urbanRepo.deleteById(messageId);
    }

    public String getQueuedExpiringMessageExpiryDate(String messageId)
    {
        return pendingRepo.findById(messageId).map(PendingDisabledMessage::getExpiryTimestamp).orElse(null);
    }

    public String getQueuedExpiringMessageChannel(String messageId)
    {
        return pendingRepo.findById(messageId).map(PendingDisabledMessage::getChannelId).orElse(null);
    }

    public String getQueuedExpiringMessageGuild(String messageId)
    {
        return pendingRepo.findById(messageId).map(PendingDisabledMessage::getGuildId).orElse(null);
    }

    public void trackUrban(String meanings, String examples, String contributors, String dates, Message message, String term)
    {
        UrbanDictionaryEntry e = new UrbanDictionaryEntry();
        e.setMessageId(message.getId());
        e.setPage(0);
        e.setMeanings(meanings);
        e.setExamples(examples);
        e.setContributors(contributors);
        e.setDates(dates);
        e.setTerm(term);
        urbanRepo.save(e);
    }

    public int getUrbanPage(String messageId)
    {
        return urbanRepo.findById(messageId)
                .map(UrbanDictionaryEntry::getPage)
                .orElse(0);
    }

    public String getUrbanMeanings(String messageId)
    {
        return urbanRepo.findById(messageId).map(UrbanDictionaryEntry::getMeanings).orElse(null);
    }

    public String getUrbanExamples(String messageId)
    {
        return urbanRepo.findById(messageId).map(UrbanDictionaryEntry::getExamples).orElse(null);
    }

    public String getUrbanContributors(String messageId)
    {
        return urbanRepo.findById(messageId).map(UrbanDictionaryEntry::getContributors).orElse(null);
    }

    public String getUrbanDates(String messageId)
    {
        return urbanRepo.findById(messageId).map(UrbanDictionaryEntry::getDates).orElse(null);
    }

    public String getUrbanTerm(String messageId)
    {
        return urbanRepo.findById(messageId).map(UrbanDictionaryEntry::getTerm).orElse(null);
    }

    public void setUrbanPage(String messageId, int page)
    {
        urbanRepo.findById(messageId).ifPresent(e -> {
            e.setPage(page);
            urbanRepo.save(e);
        });
    }

    public void resetExpiryTimestamp(String messageId)
    {
        pendingRepo.findById(messageId).ifPresent(row -> {
            String formatted = DateTimeFormatter
                    .ofPattern(Cache.getExpiryTimestampFormat())
                    .format(LocalDateTime.now().plusSeconds(Cache.getExpiryTimeSeconds()));
            row.setExpiryTimestamp(formatted);
            pendingRepo.save(row);
        });
    }
}

package wtf.beatrice.hidekobot.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pending_disabled_messages")
public class PendingDisabledMessage
{
    @Id
    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "guild_id", nullable = false)
    private String guildId;

    @Column(name = "channel_id", nullable = false)
    private String channelId;

    @Column(name = "expiry_timestamp", nullable = false)
    private String expiryTimestamp; // keep as String to match your format for now

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getGuildId()
    {
        return guildId;
    }

    public void setGuildId(String guildId)
    {
        this.guildId = guildId;
    }

    public String getChannelId()
    {
        return channelId;
    }

    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    public String getExpiryTimestamp()
    {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(String expiryTimestamp)
    {
        this.expiryTimestamp = expiryTimestamp;
    }
}

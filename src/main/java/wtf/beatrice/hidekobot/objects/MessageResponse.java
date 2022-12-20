package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

public class MessageResponse
{
    private final String content;
    private final MessageEmbed embed;

    public MessageResponse(String content, MessageEmbed embed)
    {
        this.content = content;
        this.embed = embed;
    }

    @Nullable
    public String getContent()
    {
        return content;
    }

    @Nullable
    public MessageEmbed getEmbed()
    {
        return embed;
    }
}

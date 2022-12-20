package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

public record MessageResponse(String content, MessageEmbed embed) {

    @Nullable
    public String getContent() {
        return content;
    }

    @Nullable
    public MessageEmbed getEmbed() {
        return embed;
    }
}

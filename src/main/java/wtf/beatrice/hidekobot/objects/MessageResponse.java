package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

public record MessageResponse(@Nullable String content, @Nullable MessageEmbed embed) {

}

package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.Nullable;

public record MessageResponse(@Nullable String content,
                              @Nullable MessageEmbed embed,
                              @Nullable ItemComponent... components) {

}

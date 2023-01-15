package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public record MessageResponse(@Nullable String content,
                              @Nullable MessageEmbed embed,
                              @Nullable ItemComponent... components) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageResponse response = (MessageResponse) o;
        return Objects.equals(content, response.content) &&
                Objects.equals(embed, response.embed) &&
                Arrays.equals(components, response.components);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(content, embed);
        result = 31 * result + Arrays.hashCode(components);
        return result;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "content=" + content +
                ", embed=" + embed +
                ", components=" + Arrays.toString(components) +
                '}';
    }

}

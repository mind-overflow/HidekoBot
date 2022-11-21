package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class SayCommand
{

    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        MessageChannel channel = event.getChannel();

        // get the text to send
        OptionMapping textOption = event.getOption("text");
        String messageContent = "";
        if(textOption != null)
        {
             messageContent = textOption.getAsString();
        }

        if(textOption == null || messageContent.isEmpty())
        {
            event.reply("Hey, you have to tell me what to say!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        channel.sendMessage(messageContent).queue();
        event.reply("Message sent!")
                .setEphemeral(true)
                .queue();
    }
}

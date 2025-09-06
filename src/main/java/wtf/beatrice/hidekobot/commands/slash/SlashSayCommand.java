package wtf.beatrice.hidekobot.commands.slash;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.commands.base.Say;
import wtf.beatrice.hidekobot.objects.commands.SlashCommandImpl;

@Component
public class SlashSayCommand extends SlashCommandImpl
{
    private final Say say;

    public SlashSayCommand(@NotNull Say say)
    {
        this.say = say;
    }

    @Override
    public CommandData getSlashCommandData()
    {

        return Commands.slash("say", "Make the bot say something.")
                .addOption(OptionType.STRING, "text",
                        "The message to send.",
                        true,
                        false)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(say.getPermission()));
    }

    @Override
    public void runSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        MessageChannel channel = event.getChannel();

        // get the text to send
        OptionMapping textOption = event.getOption("text");
        String messageContent = "";
        if (textOption != null)
        {
            messageContent = textOption.getAsString();
        }

        if (textOption == null || messageContent.isEmpty())
        {
            event.reply("\uD83D\uDE20 Hey, you have to tell me what to say!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        channel.sendMessage(messageContent).queue();
        event.reply("Message sent! ✨")
                .setEphemeral(true)
                .queue();
    }
}

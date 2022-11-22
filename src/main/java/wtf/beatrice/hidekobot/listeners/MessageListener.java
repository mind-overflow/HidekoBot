package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.util.Logger;

public class MessageListener extends ListenerAdapter
{

    private final String commandRegex = "(?i)^(hideko|hde)\\b";
    // (?i) -> case insensitive flag
    // ^ -> start of string (not in middle of a sentence)
    // \b -> the word has to end here
    // .* -> there can be anything else after this word

    private final Logger logger = new Logger(MessageListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        String eventMessage = event.getMessage().getContentDisplay();

        if(!eventMessage.toLowerCase().matches(commandRegex + ".*"))
            return;

        MessageChannel channel = event.getChannel();
        // generate args from the string
        String argsString = eventMessage.replaceAll(commandRegex + "\\s*", "");
        String[] args = argsString.split("\\s+");

        event.getMessage().reply("Hi").queue();


        if(eventMessage.equalsIgnoreCase("hideko"))
        {
            channel.sendMessage("Hello there! ✨").queue();
            return;
        }

        if(eventMessage.equalsIgnoreCase("ping"))
        {
            channel.sendMessage("Pong!").queue();
            return;
        }

        if(eventMessage.equalsIgnoreCase("hideko verbose"))
        {
            boolean verbose = Cache.isVerbose();

            String msg = verbose ? "off" : "on";
            msg = "Turning verbosity " + msg + "!";

            Cache.setVerbose(!verbose);

            channel.sendMessage(msg).queue();
            logger.log(msg);

            return;
        }
    }
}

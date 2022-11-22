package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.objects.MessageCommand;
import wtf.beatrice.hidekobot.objects.MessageCommandAliasesComparator;
import wtf.beatrice.hidekobot.util.Logger;

import java.util.*;

public class MessageCommandListener extends ListenerAdapter
{

    // map storing command labels and command object alphabetically.
    private final TreeMap<LinkedList<String>, MessageCommand> registeredCommands =
            new TreeMap<LinkedList<String>, MessageCommand>(new MessageCommandAliasesComparator());

    private final String commandRegex = "(?i)^(hideko|hde)\\b";
    // (?i) -> case insensitive flag
    // ^ -> start of string (not in middle of a sentence)
    // \b -> the word has to end here
    // .* -> there can be anything else after this word


    public void registerCommand(MessageCommand command)
    {
        registeredCommands.put(command.getCommandLabels(), command);
    }

    public MessageCommand getRegisteredCommand(String label)
    {
        for(LinkedList<String> aliases : registeredCommands.keySet())
        {
            for(String currentAlias : aliases)
            {
                if(label.equals(currentAlias))
                { return registeredCommands.get(aliases); }
            }
        }

        return null;
    }


    public LinkedList<MessageCommand> getRegisteredCommands()
    { return new LinkedList<>(registeredCommands.values()); }


    private final Logger logger = new Logger(MessageCommandListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        String eventMessage = event.getMessage().getContentDisplay();

        if(!eventMessage.toLowerCase().matches(commandRegex + ".*"))
            return;

        MessageChannel channel = event.getChannel();
        // generate args from the string
        String argsString = eventMessage.replaceAll(commandRegex + "\\s*", "");


        // if no args were specified apart from the bot prefix
        // note: we can't check argsRaw's size because String.split returns an array of size 1 if no match is found,
        // and that element is the whole string passed as a single argument, which would be empty in this case
        // (or contain text in other cases like "string split ," if the passed text doesn't contain any comma ->
        // it will be the whole text as a single element.
        if(argsString.isEmpty())
        {
            event.getMessage().reply("Hello there! ✨").queue();
            return;
        }

        // split all passed arguments
        String[] argsRaw = argsString.split("\\s+");

        // extract the command that the user is trying to run
        String commandLabel = argsRaw[0];
        MessageCommand commandObject = getRegisteredCommand(commandLabel);

        if(commandObject != null)
        {
            String[] commandArgs;
            if(commandObject.passRawArgs())
            {
                // remove first argument, which is the command label
                argsString = argsString.replaceAll("^[\\S]+\\s+", "");
                // pass all other arguments as a single argument as the first array element
                commandArgs = new String[]{argsString};
            }
            else
            {
                // copy all split arguments to the array, except from the command label
                commandArgs = Arrays.copyOfRange(argsRaw, 1, argsRaw.length);
            }
            commandObject.runCommand(event, commandLabel, commandArgs);
        } else {
            event.getMessage().reply("Unrecognized command: `" + commandLabel + "`!").queue(); // todo prettier
        }
    }
}
